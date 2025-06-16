#!/bin/bash

if [ $# -eq 0 ]; then
    echo "Usage: $0 <version>"
    echo "Example: $0 2.9.1"
    exit 1
fi

VERSION="$1"
CHANGELOG_FILE="CHANGELOG.md"
OUTPUT_FILE="CHANGELOG-${VERSION}.md"

if [ ! -f "$CHANGELOG_FILE" ]; then
    echo "Error: file $CHANGELOG_FILE not found"
    exit 1
fi

echo "Creating $OUTPUT_FILE for version $VERSION..."

# Сначала проверяем, есть ли версия в файле
if ! grep -q "^## $VERSION$" "$CHANGELOG_FILE"; then
    echo "Error: version $VERSION not found in $CHANGELOG_FILE"
    exit 1
fi

# Только если версия найдена, создаем файл
awk -v version="$VERSION" '
BEGIN {
    found = 0
    print_section = 0
    skip_empty_line = 0
}

/^## / {
    if ($2 == version) {
        found = 1
        print_section = 1
        skip_empty_line = 1
        next
    } else if (print_section) {
        print_section = 0
        exit
    }
}

print_section == 1 {
    if (skip_empty_line && NF == 0) {
        skip_empty_line = 0
        next
    }
    skip_empty_line = 0
    print $0
}
' "$CHANGELOG_FILE" > "$OUTPUT_FILE"

# Удаляем пустые строки в конце файла
if [ -f "$OUTPUT_FILE" ]; then
    printf '%s\n' "$(sed -e :a -e '/^\s*$/N;ta' -e 's/\n*$//' "$OUTPUT_FILE")" > "$OUTPUT_FILE"
fi

# Проверяем, что файл создан и не пустой
if [ -s "$OUTPUT_FILE" ]; then
    echo "Successfully created file $OUTPUT_FILE"
    echo "Contents:"
    echo "----------------------------------------"
    cat "$OUTPUT_FILE"
else
    echo "Error: failed to extract content for version $VERSION"
    rm -f "$OUTPUT_FILE"
    exit 1
fi
