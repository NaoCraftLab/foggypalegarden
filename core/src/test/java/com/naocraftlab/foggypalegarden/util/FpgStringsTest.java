package com.naocraftlab.foggypalegarden.util;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FpgStringsTest implements WithAssertions {

    @Nested
    class WildcardMatchTest {

        @ParameterizedTest
        @CsvSource({
                // точное совпадение
                "'minecraft:pale_garden', 'minecraft:pale_garden'",

                // вайлдкарта в конце
                "'minecraft:pale_garden', 'minecraft:*'",
                "'minecraft:pale_garden', 'minecraft:pale*'",

                // вайлдкарта в начале
                "'minecraft:pale_garden', '*:pale_garden'",
                "'minecraft:pale_garden', '*pale_garden'",

                // вайлдкарта в середине
                "'minecraft:pale_garden', 'minecraft:*garden'",
                "'minecraft:pale_garden', 'mine*pale_garden'",
                "'minecraft:pale_garden', 'minecraft*garden'",

                // множественные вайлдкарты
                "'minecraft:pale_garden', '*:*'",
                "'minecraft:pale_garden', 'mine*:*garden'",
                "'minecraft:pale_garden', 'm*t:pale*n'",
                "'minecraft:pale_garden', '*:*_*'",

                // последовательные вайлдкарты
                "'minecraft:pale_garden', 'm**t:pale_garden'",
                "'minecraft:pale_garden', 'minecraft:**garden'",
                "'minecraft:pale_garden', '***:pale_garden'",

                // вайлдкарта сопоставляется с пустой строкой
                "'minecraft:pale_garden', 'minecraft*:pale_garden'",

                // специальные случаи
                "'minecraft:pale_garden', '*'",
                "'minecraft:pale_garden', 'm*'",
                "'minecraft:pale_garden', '*n'"
        })
        void shouldMatchWildcardPatterns(String text, String pattern) {
            assertThat(FpgStrings.wildcardMatch(text, pattern)).isTrue();
        }

        @ParameterizedTest
        @CsvSource({
                // Несоответствие в начале строки
                "'minecraft:pale_garden', 'notmine:pale_garden'",
                "'minecraft:pale_garden', 'zzz*:pale_garden'",

                // Несоответствие в конце строки
                "'minecraft:pale_garden', 'minecraft:other'",
                "'minecraft:pale_garden', 'minecraft:pale_garden_extra'",

                // Несоответствие в середине строки
                "'minecraft:pale_garden', 'minecraft:dark_garden'",
                "'minecraft:pale_garden', 'minecraft:pale-garden'",

                // Шаблон короче текста (без вайлдкарты в конце)
                "'minecraft:pale_garden', 'minecraft:pale'",
                "'minecraft:pale_garden', 'mine'",

                // Шаблон длиннее текста
                "'minecraft:pale_garden', 'minecraft:pale_garden_extra_long'",

                // Пустые строки
                "'', 'minecraft'",
                "'minecraft:pale_garden', ''",

                // Регистр имеет значение
                "'minecraft:pale_garden', 'Minecraft:pale_garden'",
                "'minecraft:pale_garden', 'minecraft:Pale_garden'",

                // Вайлдкарта не соответствует границам
                "'minecraft:pale_garden', '*craft:water*'",
                "'minecraft:pale_garden', 'mine*:*_forest'",

                // Вайлдкарта в неправильной позиции
                "'minecraft:pale_garden', 'min*craft:tale_garden'",
                "'minecraft:pale_garden', 'minecraft:pale*field'",

                // Односимвольное несоответствие
                "'minecraft:pale_garden', 'minecrafts:pale_garden'",
                "'minecraft:pale_garden', 'minecraft:palex_garden'",

                // Шаблон с вайлдкартами, но всё равно несовпадающий
                "'minecraft:pale_garden', '*:water*'",
                "'minecraft:pale_garden', 'm*:*_tree'",

                // Краевые случаи с вайлдкартами
                "'minecraft:pale_garden', '**minecraft'",
                "'minecraft:pale_garden', '*mine*water*'"
        })
        void shouldNotMatchWildcardPatterns(String text, String pattern) {
            assertThat(FpgStrings.wildcardMatch(text, pattern)).isFalse();
        }
    }
}
