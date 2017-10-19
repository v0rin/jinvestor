package org.jinvestor.datasource.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.datasource.converter.FastRawResultSetToPojoConverter;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.model.entity.IEntityMetaData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class FastRawResultSetToPojoConverterTest {

	private static final Logger LOG = LogManager.getLogger();

	private static final String TEST_RES_PATH = "datasource/sqlite/";
	private static final String DB_PATH = TEST_RES_PATH + "test-big.sqlite";
	private static final String DB_CONNECTION_STRING_PREFIX = "jdbc:sqlite:";
	private static final String DB_CONNECTION_STRING = DB_CONNECTION_STRING_PREFIX + DB_PATH;

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	ResultSet resultSet;

	@Mock
	ResultSetMetaData resultSetMetaData;

	private TestEntity expectedTestEntity;
	FastRawResultSetToPojoConverter<TestEntity> converter;


	@Before
	public void setUp() throws SQLException {
		IEntityMetaData<TestEntity> entityMetaData = EntityMetaDataFactory.get(TestEntity.class);
		String[] entityColumns = entityMetaData.getColumns();

		when(resultSetMetaData.getColumnCount()).thenReturn(entityColumns.length);
		when(resultSetMetaData.getColumnName(1)).thenReturn(entityColumns[0]);
		when(resultSetMetaData.getColumnName(2)).thenReturn(entityColumns[1]);

		when(resultSet.getMetaData()).thenReturn(resultSetMetaData);

		expectedTestEntity = new TestEntity();
		expectedTestEntity.setDateTime(Instant.now());
		expectedTestEntity.setPrice(1.1);
		when(resultSet.getObject(1)).thenReturn(expectedTestEntity.dateTime);
		when(resultSet.getObject(2)).thenReturn(expectedTestEntity.price);

		converter = new FastRawResultSetToPojoConverter<TestEntity>(TestEntity.class);
	}


	@Test
	public void test() {
		// given

		// when
		TestEntity actualTestEntity = converter.apply(resultSet);

		// then
		assertThat(actualTestEntity, is(expectedTestEntity));
	}

	@Entity
	@Table(name="test_entities")
	public static class TestEntity {

		@Column(name="datetime", columnDefinition="TEXT")
		public Instant dateTime;

		@Column(name="price", columnDefinition="REAL")
		public Double price;

		public void setDateTime(Instant dateTime) {
			this.dateTime = dateTime;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(1094259991, 484931225)
					.append(this.dateTime)
					.append(this.price)
					.toHashCode();
		}

		@Override
		public boolean equals(Object object) {
			if (object == this) { return true; }
			if (!(object instanceof TestEntity)) {
				return false;
			}
			TestEntity rhs = (TestEntity) object;
			return new EqualsBuilder()
						.append(this.dateTime, rhs.dateTime)
						.append(this.price, rhs.price).isEquals();
		}

		@Override
		public String toString() {
			return "TestEntity [dateTime=" + dateTime + ", price=" + price + "]";
		}

	}
}
