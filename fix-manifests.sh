#!/bin/bash

echo "🔍 در حال جستجوی فایل‌های AndroidManifest.xml دارای package..."

# لیست فایل‌هایی که اصلاح شدن
MODIFIED_FILES=()

# جستجو در کل پروژه
find . -type f -name "AndroidManifest.xml" | while read manifest; do
  if grep -q 'package=' "$manifest"; then
    echo "✂️ حذف package از: $manifest"
    sed -i '/package=/d' "$manifest"
    MODIFIED_FILES+=("$manifest")
  fi
done

# گزارش نهایی
echo ""
if [ ${#MODIFIED_FILES[@]} -eq 0 ]; then
  echo "✅ هیچ فایل AndroidManifest.xml دارای package پیدا نشد. همه‌چی مرتبه!"
else
  echo "📄 فایل‌های اصلاح‌شده:"
  for file in "${MODIFIED_FILES[@]}"; do
    echo "  - $file"
  done
fi
