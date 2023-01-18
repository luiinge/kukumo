/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package iti.kukumo.groovy;

import iti.kukumo.api.KukumoException;
import iti.kukumo.api.extensions.PropertyEvaluator;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class GroovyPropertyEvaluatorTest {

    PropertyEvaluator evaluator = new GroovyPropertyEvaluator();

    @Test
    public void test() {
        PropertyEvaluator.Result result = evaluator.eval("${=2+2}");
        assertThat(result.value()).isEqualTo("4");

        result = evaluator.eval("Today is ${=new Date().format('yyyy-MM-dd')}");
        assertThat(result.value())
                .isEqualTo("Today is " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @Test(expected = KukumoException.class)
    public void testWithError() {
        evaluator.eval("${=assert false}");
    }
}
