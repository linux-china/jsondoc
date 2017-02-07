package org.jsondoc.springmvc.scanner;

import com.google.common.collect.Sets;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiDoc;
import org.jsondoc.core.pojo.ApiMethodDoc;
import org.jsondoc.core.pojo.ApiVerb;
import org.jsondoc.core.pojo.JSONDoc.MethodDisplay;
import org.jsondoc.core.scanner.JSONDocScanner;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Set;

public class SpringApiVerbDocTest {

    private JSONDocScanner jsondocScanner = new Spring3JSONDocScanner();

    @Controller
    @RequestMapping(value = "/api-verb")
    public class SpringApiVerbController {

        //@RequestMapping(value = "/spring-api-verb-controller-method-one")
        @GetMapping("/spring-api-verb-controller-method-one")
        @ApiMethod(id = "apiVerbOne", description = "spring-api-verb-controller-method-one", summary = "get statics")
        public void apiVerbOne() {

        }

        @RequestMapping(value = "/spring-api-verb-controller-method-two", method = {RequestMethod.POST, RequestMethod.GET})
        @ApiMethod(id = "apiVerbTwo", description = "spring-api-verb-controller-method-one", summary = "get statics")
        public void apiVerbTwo() {

        }

    }

    @Controller
    @RequestMapping(value = "/api-verb-2", method = {RequestMethod.POST, RequestMethod.PUT})
    public class SpringApiVerbController2 {

        @RequestMapping(value = "/spring-api-verb-controller-method-one")
        public void apiVerbOne() {

        }

    }

    @Test
    public void testApiVerb() {
        ApiDoc apiDoc = jsondocScanner.getApiDocs(Sets.<Class<?>>newHashSet(SpringApiVerbController.class), MethodDisplay.URI).iterator().next();
        Assert.assertEquals("SpringApiVerbController", apiDoc.getName());
        Assert.assertEquals(2, apiDoc.getMethods().size());
        for (ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/api-verb/spring-api-verb-controller-method-one")) {
                Assert.assertEquals(1, apiMethodDoc.getVerb().size());
                Assert.assertEquals(ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
            }
            if (apiMethodDoc.getPath().contains("/api-verb/spring-api-verb-controller-method-two")) {
                Assert.assertEquals(2, apiMethodDoc.getVerb().size());
            }
        }

        apiDoc = jsondocScanner.getApiDocs(Sets.<Class<?>>newHashSet(SpringApiVerbController2.class), MethodDisplay.URI).iterator().next();
        Assert.assertEquals("SpringApiVerbController2", apiDoc.getName());
        Assert.assertEquals(1, apiDoc.getMethods().size());
        for (ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/api-verb-2/spring-api-verb-controller-method-one")) {
                Assert.assertEquals(2, apiMethodDoc.getVerb().size());
            }
        }

    }


    @Test
    public void testAnnotation() throws Exception {
        Method apiVerbOne = SpringApiVerbController.class.getMethod("apiVerbOne");
        RequestMapping requestMapping = AnnotatedElementUtils.getMergedAnnotation(apiVerbOne, RequestMapping.class);
        System.out.println(requestMapping.path()[0]);
    }
}
