#!/usr/bin/env python3
"""
Generate repository statistics for Android Fourteeners project and update README.md.

Outputs a Markdown section with Mermaid pie charts and summary tables between markers:

<!-- BEGIN: REPO-STATS -->
... generated ...
<!-- END: REPO-STATS -->

Run: python3 scripts/generate_repo_stats.py
"""
from __future__ import annotations

import os
import sys
from pathlib import Path
from typing import Dict, Iterable, Tuple

REPO_ROOT = Path(__file__).resolve().parents[1]
README_PATH = REPO_ROOT / "README.md"

# Directories to ignore when scanning
IGNORE_DIRS = {
    ".git",
    ".gradle",
    "build",
    ".idea",
    "gradle",
    "__pycache__",
    ".pytest_cache",
    "node_modules",
}

# File extensions to consider and their comment markers
COMMENT_PREFIX = {
    ".kt": "//",       # Kotlin
    ".java": "//",     # Java
    ".xml": "<!--",    # XML layouts and resources
    ".gradle": "//",   # Gradle build files
    ".pro": "#",       # ProGuard rules
    ".properties": "#", # Properties files
    ".sh": "#",        # Shell scripts
    ".py": "#",        # Python scripts
    ".yml": "#",       # YAML configs
    ".yaml": "#",
    ".md": "",         # Markdown (count all non-blank as code)
}

CONSIDER_EXTS = set(COMMENT_PREFIX.keys()) | {
    ".txt",
    ".json",
}


def is_ignored(path: Path) -> bool:
    """Check if path should be ignored."""
    return any(part in IGNORE_DIRS for part in path.parts)


def count_file(path: Path) -> Tuple[int, int]:
    """Return (total_lines, code_lines) for a given file.

    Code lines exclude blank lines and comment lines.
    """
    try:
        text = path.read_text(encoding="utf-8", errors="ignore")
    except Exception:
        return 0, 0

    total = 0
    code = 0
    prefix = COMMENT_PREFIX.get(path.suffix)

    in_block_comment = False

    for raw in text.splitlines():
        total += 1
        line = raw.strip()

        # Skip blank lines
        if not line:
            continue

        # Handle block comments for Java/Kotlin
        if path.suffix in {".kt", ".java", ".gradle"}:
            if "/*" in line:
                in_block_comment = True
            if in_block_comment:
                if "*/" in line:
                    in_block_comment = False
                continue

        # Handle XML block comments
        if path.suffix == ".xml":
            if "<!--" in line and "-->" in line:
                continue  # Single line comment
            if "<!--" in line:
                in_block_comment = True
            if in_block_comment:
                if "-->" in line:
                    in_block_comment = False
                continue

        # Skip single line comments
        if prefix and line.startswith(prefix):
            continue

        code += 1

    return total, code


def iter_repo_files(root: Path) -> Iterable[Path]:
    """Iterate over all relevant files in the repository."""
    for p in root.rglob("*"):
        if p.is_dir():
            continue
        if is_ignored(p):
            continue
        if p.suffix.lower() in CONSIDER_EXTS:
            yield p


def aggregate_stats() -> Dict:
    """Aggregate statistics by area and file type."""
    by_area: Dict[str, Dict[str, int]] = {}
    by_type: Dict[str, Dict[str, int]] = {}
    by_module: Dict[str, Dict[str, int]] = {}

    def bump(bucket: Dict[str, Dict[str, int]], key: str, total: int, code: int) -> None:
        d = bucket.setdefault(key, {"total": 0, "code": 0, "files": 0})
        d["total"] += total
        d["code"] += code
        d["files"] += 1

    for f in iter_repo_files(REPO_ROOT):
        total, code = count_file(f)

        # By file type
        ext = f.suffix.lower()
        if ext in {".kt", ".java"}:
            type_key = "Kotlin/Java"
        elif ext == ".xml":
            type_key = "XML"
        elif ext in {".gradle", ".properties"}:
            type_key = "Build Config"
        elif ext == ".md":
            type_key = "Documentation"
        else:
            type_key = "Other"
        bump(by_type, type_key, total, code)

        # By area
        parts = f.relative_to(REPO_ROOT).parts
        if not parts:
            area = "root"
        elif parts[0] == "app":
            if len(parts) > 2:
                if parts[1] == "src":
                    if parts[2] == "main":
                        if "kotlin" in str(f) or "java" in str(f):
                            area = "app/src/main (code)"
                        else:
                            area = "app/src/main (resources)"
                    elif parts[2] == "androidTest":
                        area = "app/src/androidTest"
                    elif parts[2] == "test":
                        area = "app/src/test"
                    else:
                        area = "app/other"
                else:
                    area = "app/other"
            else:
                area = "app/other"
        elif parts[0] == "docs":
            area = "docs"
        elif parts[0] == "scripts":
            area = "scripts"
        else:
            area = "other"
        bump(by_area, area, total, code)

        # By module (for Kotlin/Java files in main source)
        try:
            if f.suffix in {".kt", ".java"}:
                rel_path = f.relative_to(REPO_ROOT / "app" / "src" / "main")
                if "kotlin" in str(rel_path) or "java" in str(rel_path):
                    # Extract package/module
                    parts = list(rel_path.parts)
                    if "fourteeners" in parts:
                        idx = parts.index("fourteeners")
                        if idx < len(parts) - 1:
                            module = parts[idx + 1]
                            if module.endswith(".kt") or module.endswith(".java"):
                                module = "root"
                            bump(by_module, module, total, code)
        except ValueError:
            pass

    return {
        "by_area": by_area,
        "by_type": by_type,
        "by_module": by_module,
        "overall": {
            "total": sum(v["total"] for v in by_area.values()),
            "code": sum(v["code"] for v in by_area.values()),
            "files": sum(v["files"] for v in by_area.values()),
        }
    }


def format_number(n: int) -> str:
    """Format number with thousands separator."""
    return f"{n:,}"


def generate_markdown(stats: Dict) -> str:
    """Generate markdown content with statistics."""
    by_area = stats["by_area"]
    by_type = stats["by_type"]
    by_module = stats["by_module"]
    overall = stats["overall"]

    # Mermaid pie chart for file types
    type_pie = ["```mermaid", "pie title Code LOC by File Type"]
    for name, data in sorted(by_type.items(), key=lambda kv: kv[1]["code"], reverse=True):
        if data["code"] > 0:
            type_pie.append(f'  "{name}" : {data["code"]}')
    type_pie.append("```")

    # Mermaid pie chart for areas
    area_pie = ["```mermaid", "pie title Code LOC by Area"]
    for name, data in sorted(by_area.items(), key=lambda kv: kv[1]["code"], reverse=True):
        if data["code"] > 0:
            area_pie.append(f'  "{name}" : {data["code"]}')
    area_pie.append("```")

    # Tables
    type_table = [
        "| File Type | Files | Code LOC | Total Lines |",
        "|-----------|-------|----------|-------------|"
    ]
    for name, data in sorted(by_type.items(), key=lambda kv: kv[1]["code"], reverse=True):
        type_table.append(
            f"| {name} | {data['files']} | {format_number(data['code'])} | {format_number(data['total'])} |"
        )

    area_table = [
        "| Area | Files | Code LOC | Total Lines |",
        "|------|-------|----------|-------------|"
    ]
    for name, data in sorted(by_area.items(), key=lambda kv: kv[1]["code"], reverse=True):
        if data['files'] > 0:
            area_table.append(
                f"| {name} | {data['files']} | {format_number(data['code'])} | {format_number(data['total'])} |"
            )

    module_table = [
        "| Module | Files | Code LOC | Total Lines |",
        "|--------|-------|----------|-------------|"
    ]
    for name, data in sorted(by_module.items(), key=lambda kv: kv[1]["code"], reverse=True):
        module_table.append(
            f"| {name} | {data['files']} | {format_number(data['code'])} | {format_number(data['total'])} |"
        )

    # Build final markdown
    lines = []
    lines.append("## Repository Statistics")
    lines.append("")
    lines.append("### Overview")
    lines.append(f"- **Total Files**: {format_number(overall['files'])}")
    lines.append(f"- **Code Lines**: {format_number(overall['code'])}")
    lines.append(f"- **Total Lines**: {format_number(overall['total'])}")
    lines.append("")

    lines.append("### Code Distribution by File Type")
    lines.extend(type_pie)
    lines.append("")
    lines.extend(type_table)
    lines.append("")

    lines.append("### Code Distribution by Area")
    lines.extend(area_pie)
    lines.append("")
    lines.extend(area_table)
    lines.append("")

    if by_module:
        lines.append("### Kotlin/Java Modules")
        lines.extend(module_table)
        lines.append("")

    lines.append("_Generated by `make repo-stats`. LOC counts exclude blank lines and comments._")

    return "\n".join(lines)


def update_readme(new_section: str) -> None:
    """Update README.md with the generated statistics section."""
    begin = "<!-- BEGIN: REPO-STATS -->"
    end = "<!-- END: REPO-STATS -->"
    generated = f"{begin}\n\n{new_section}\n\n{end}"

    if not README_PATH.exists():
        # Create file with section if missing
        README_PATH.write_text(generated, encoding="utf-8")
        print("Created README.md with stats section")
        return

    content = README_PATH.read_text(encoding="utf-8")
    if begin in content and end in content:
        # Replace existing section
        pre = content.split(begin)[0]
        post = content.split(end)[-1]
        README_PATH.write_text(pre + generated + post, encoding="utf-8")
        print("Updated existing stats section in README.md")
    else:
        # Append new section
        sep = "\n\n---\n\n"
        README_PATH.write_text(content.rstrip() + sep + generated + "\n", encoding="utf-8")
        print("Added new stats section to README.md")


def main() -> None:
    """Main entry point."""
    print("📊 Generating repository statistics...")
    stats = aggregate_stats()
    md = generate_markdown(stats)
    update_readme(md)
    print("✅ Repository stats updated in README.md")


if __name__ == "__main__":
    main()