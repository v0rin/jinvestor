package org.jinvestor.model;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.model.Bar;
import org.jinvestor.model.EntityUtil;
import org.junit.Test;

public class EntityUtilTest {

	private static final Logger LOG = LogManager.getLogger();

	@Test
	public void test() {
		LOG.debug(EntityUtil.getTableName(Bar.class));
		LOG.debug(Arrays.asList(EntityUtil.getColumns(Bar.class)));
	}

}
