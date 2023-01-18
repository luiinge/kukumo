/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package iti.kukumo.test.properties;

import com.fasterxml.jackson.databind.JsonNode;
import iti.kukumo.api.Backend;
import iti.kukumo.api.KukumoException;
import iti.kukumo.api.KukumoStepRunContext;
import iti.kukumo.api.extensions.PropertyEvaluator;
import iti.kukumo.api.util.JsonUtils;
import iti.kukumo.api.util.XmlUtils;
import iti.kukumo.core.properties.StepPropertyEvaluator;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StepPropertyEvaluatorTest {

    private final StepPropertyEvaluator resolver = new StepPropertyEvaluator();
    private final JsonNode json = JsonUtils.json(Map.of("id", 3, "user", "pepe"));
    private final XmlObject xml = XmlUtils.xml("response", Map.of("id", 3, "user", "pepe"));

    @Before
    public void setup() {
        List<Object> results = Arrays.asList(null, json, xml, json.toString(), xml.toString());

        KukumoStepRunContext context = mock(KukumoStepRunContext.class);
        Backend backend = mock(Backend.class);
        when(backend.getResults()).thenReturn(results);
        when(context.backend()).thenReturn(backend);
        KukumoStepRunContext.set(context);
    }

    @Test
    public void testResolverWhenJsonNodeResponseWithSuccess() {
        PropertyEvaluator.Result result = resolver.eval("'${2#$.user}'");
        assertThat(result.value()).isEqualTo("'pepe'");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${2#$.user}", "pepe"
        ));

        result = resolver.eval("'user${2#$.id}'");
        assertThat(result.value()).isEqualTo("'user3'");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${2#$.id}", "3"
        ));

        result = resolver.eval("${2#$.id}");
        assertThat(result.value()).isEqualTo("3");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${2#$.id}", "3"
        ));

        result = resolver.eval("${2#$.id}${2#$.id}");
        assertThat(result.value()).isEqualTo("33");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${2#$.id}", "3"
        ));

        result = resolver.eval("${2#}");
        assertThat(result.value()).isEqualTo(json.toString());
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${2#}", json.toString()
        ));
    }

    @Test
    public void testResolverWhenJsonStringResponseWithSuccess() {
        PropertyEvaluator.Result result = resolver.eval("'${4#$.user}'");
        assertThat(result.value()).isEqualTo("'pepe'");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${4#$.user}", "pepe"
        ));

        result = resolver.eval("'user${4#$.id}'");
        assertThat(result.value()).isEqualTo("'user3'");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${4#$.id}", "3"
        ));

        result = resolver.eval("${4#$.id}");
        assertThat(result.value()).isEqualTo("3");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${4#$.id}", "3"
        ));

        result = resolver.eval("${4#$.id}${4#$.id}");
        assertThat(result.value()).isEqualTo("33");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${4#$.id}", "3"
        ));

        result = resolver.eval("${4#}");
        assertThat(result.value()).isEqualTo(json.toString());
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${4#}", json.toString()
        ));
    }

    @Test
    public void testResolverWhenXmlObjectResponseWithSuccess() {
        PropertyEvaluator.Result result = resolver.eval("'${3#//user/text()}'");
        assertThat(result.value()).isEqualTo("'pepe'");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${3#//user/text()}", "pepe"
        ));

        result = resolver.eval("'user${3#//id/text()}'");
        assertThat(result.value()).isEqualTo("'user3'");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${3#//id/text()}", "3"
        ));

        result = resolver.eval("${3#//id/text()}");
        assertThat(result.value()).isEqualTo("3");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${3#//id/text()}", "3"
        ));

        result = resolver.eval("${3#//id/text()}${3#//id/text()}");
        assertThat(result.value()).isEqualTo("33");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${3#//id/text()}", "3"
        ));

        result = resolver.eval("${3#}");
        assertThat(result.value()).isEqualTo(xml.toString());
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${3#}", xml.toString()
        ));
    }

    @Test
    public void testResolverWhenXmlStringResponseWithSuccess() {
        PropertyEvaluator.Result result = resolver.eval("'${5#//user/text()}'");
        assertThat(result.value()).isEqualTo("'pepe'");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${5#//user/text()}", "pepe"
        ));

        result = resolver.eval("'user${5#//id/text()}'");
        assertThat(result.value()).isEqualTo("'user3'");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${5#//id/text()}", "3"
        ));

        result = resolver.eval("${5#//id/text()}");
        assertThat(result.value()).isEqualTo("3");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${5#//id/text()}", "3"
        ));

        result = resolver.eval("${5#//id/text()}${5#//id/text()}");
        assertThat(result.value()).isEqualTo("33");
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${5#//id/text()}", "3"
        ));

        result = resolver.eval("${5#}");
        assertThat(result.value()).isEqualTo(xml.toString());
        assertThat(result.evaluations()).containsExactlyEntriesOf(Map.of(
                "${5#}", xml.toString()
        ));
    }

    @Test(expected = KukumoException.class)
    public void testResolveWhenResultNullWithError() {
        resolver.eval("'${1#$.user}'");
    }

    @Test(expected = KukumoException.class)
    public void testResolveWhenNotValidOperationWithError() {
        resolver.eval("'${2#//user}'");
    }
}
