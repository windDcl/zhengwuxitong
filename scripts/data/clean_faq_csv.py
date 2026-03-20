#!/usr/bin/env python3
import csv
import sys
from collections import Counter

ALLOWED_CATEGORIES = {"社保", "公积金", "计划生育"}
ALLOWED_STATUS = {"启用", "停用"}


def norm(s: str) -> str:
    return (s or "").strip()


def main(inp: str, out: str, report: str) -> int:
    rows = []
    errors = []
    seen = set()
    cat_counter = Counter()

    with open(inp, "r", encoding="utf-8-sig", newline="") as f:
        reader = csv.DictReader(f)
        required = ["分类", "标准问题", "相似问法1", "相似问法2", "相似问法3", "标准答案", "状态"]
        if reader.fieldnames != required:
            errors.append(f"列头不匹配，期望: {required}, 实际: {reader.fieldnames}")
        for idx, r in enumerate(reader, start=2):
            cat = norm(r.get("分类"))
            q = norm(r.get("标准问题"))
            a1 = norm(r.get("相似问法1"))
            a2 = norm(r.get("相似问法2"))
            a3 = norm(r.get("相似问法3"))
            ans = norm(r.get("标准答案"))
            status = norm(r.get("状态")) or "启用"

            if cat not in ALLOWED_CATEGORIES:
                errors.append(f"第{idx}行 分类非法: {cat}")
            if not q:
                errors.append(f"第{idx}行 标准问题为空")
            if not ans:
                errors.append(f"第{idx}行 标准答案为空")
            if status not in ALLOWED_STATUS:
                errors.append(f"第{idx}行 状态非法: {status}")

            dedup_key = (cat, q)
            if dedup_key in seen:
                errors.append(f"第{idx}行 重复标准问题: {cat}/{q}")
            else:
                seen.add(dedup_key)

            aliases = []
            for x in [a1, a2, a3]:
                if x and x not in aliases:
                    aliases.append(x)

            row = {
                "分类": cat,
                "标准问题": q,
                "相似问法1": aliases[0] if len(aliases) > 0 else "",
                "相似问法2": aliases[1] if len(aliases) > 1 else "",
                "相似问法3": aliases[2] if len(aliases) > 2 else "",
                "标准答案": ans,
                "状态": status,
            }
            rows.append(row)
            if cat:
                cat_counter[cat] += 1

    with open(out, "w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=["分类", "标准问题", "相似问法1", "相似问法2", "相似问法3", "标准答案", "状态"])
        writer.writeheader()
        writer.writerows(rows)

    with open(report, "w", encoding="utf-8") as f:
        f.write("# FAQ 清洗报告\n\n")
        f.write(f"总行数: {len(rows)}\n\n")
        f.write("分类分布:\n")
        for k in ["社保", "公积金", "计划生育"]:
            f.write(f"- {k}: {cat_counter.get(k, 0)}\n")
        f.write("\n错误列表:\n")
        if errors:
            for e in errors:
                f.write(f"- {e}\n")
        else:
            f.write("- 无\n")

    print(f"cleaned={out}")
    print(f"report={report}")
    print(f"errors={len(errors)}")
    return 1 if errors else 0


if __name__ == "__main__":
    if len(sys.argv) != 4:
        print("Usage: clean_faq_csv.py <input.csv> <output.csv> <report.md>")
        sys.exit(2)
    sys.exit(main(sys.argv[1], sys.argv[2], sys.argv[3]))
