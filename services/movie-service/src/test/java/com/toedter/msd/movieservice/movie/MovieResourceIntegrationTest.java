package com.toedter.msd.movieservice.movie;

import com.toedter.msd.movieservice.MovieService;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkDiscoverer;
import org.springframework.hateoas.LinkDiscoverers;
import org.springframework.hateoas.MediaTypes;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MovieService.class)
@WebAppConfiguration
@IntegrationTest({"server.port=0"})
public class MovieResourceIntegrationTest {

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected LinkDiscoverers links;
    protected MockMvc mvc;

    @Before
    public void before() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).
                addFilter(new ShallowEtagHeaderFilter()).
                build();
    }

    @Test
    public void shouldGetUsersLink() throws Exception {
        MockHttpServletResponse response2 = mvc.perform(get("/")).
                andDo(MockMvcResultHandlers.print()).
                andExpect(status().isOk()).
                andExpect(content().contentType(MediaTypes.HAL_JSON)).
                andExpect(jsonPath("_links.users.href", CoreMatchers.notNullValue())).
                andReturn().
                getResponse();

        LinkDiscoverer discoverer = links.getLinkDiscovererFor(response2.getContentType());
        Link link = discoverer.findLinkWithRel("users", response2.getContentAsString());

        mvc.perform(get(link.getHref())).
                andDo(MockMvcResultHandlers.print()).
                andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON)).
                andExpect(jsonPath("_embedded.users", CoreMatchers.notNullValue()));
    }
}
