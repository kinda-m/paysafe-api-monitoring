package com.paysafe.monitoring;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.paysafe.monitoring.api.MonitoringServiceAPI;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaysafeApiMonitoringApplicationTests {
	public static final String PATH_ROOT = "/api/v1";
	public static final String HOSTNAME = "api.test.paysafe";
	public static final String START_MONITORING = PATH_ROOT + "/start?interval=2&hostname=" + HOSTNAME;
	public static final String STOP_MONITORING = PATH_ROOT + "/stop?hostname=" + HOSTNAME;
	public static final String DISPLAY_INFO = PATH_ROOT + "/status?hostname=" + HOSTNAME;

	@Resource
	private WebApplicationContext context;

	@Resource
	private MonitoringServiceAPI monitoringServiceAPI;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	@Ignore 
	@Test
	public void testStartingMonitoing() throws Exception {
		mvc.perform(post(START_MONITORING + "0")).andExpect(status().isOk());
	}

	@Test
	public void testStoppingMonitoingForUnmoitoredHost() throws Exception {
		mvc.perform(post(STOP_MONITORING + "12")).andExpect(status().is(404));
	}

	@Test
	public void testDisplayInfoForUnmoitoredHost() throws Exception {
		mvc.perform(get(DISPLAY_INFO + "12")).andExpect(status().is(400));
	}
	
	@Ignore
	@Test
	public void testDisplayInfoForHost() throws Exception {
		// given
		monitoringServiceAPI.startMonitoring(1, HOSTNAME + "10");
	
		// expected
		MvcResult mvcResult = mvc.perform(get(DISPLAY_INFO + "10")).andExpect(status().isOk()).andReturn();
		String info = mvcResult.getResponse().getContentAsString();
		Assert.assertNotNull(info);
	}
	
	@Test
	public void testStoppingMonitoing() throws Exception {
		// given
		monitoringServiceAPI.startMonitoring(1, HOSTNAME + "9");
		try {
			Thread.sleep(10000); // to allow monitoring to be started
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// validate
		mvc.perform(post(STOP_MONITORING + "9")).andExpect(status().isOk());
	}

}
