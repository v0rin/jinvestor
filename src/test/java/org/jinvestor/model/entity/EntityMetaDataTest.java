package org.jinvestor.model.entity;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.model.Bar;
import org.junit.Test;

public class EntityMetaDataTest {

	private static final Logger LOG = LogManager.getLogger();

	@Test
	public void test() {
		// TODO (AF) as below + negative cases
		IEntityMetaData<Bar> barMetaData = EntityMetaDataFactory.get(Bar.class);
		LOG.debug(barMetaData.getTableName());
		LOG.debug(Arrays.asList(barMetaData.getColumns()));
		LOG.debug(barMetaData.getCreateTableSql());
	}

}
