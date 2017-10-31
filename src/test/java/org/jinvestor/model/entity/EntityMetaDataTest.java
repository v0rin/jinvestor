package org.jinvestor.model.entity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.Test;

public class EntityMetaDataTest {

    private static final String EXPECTED_TABLE_NAME = "testentity";
    private static final String[] EXPECTED_COLUMNS = new String[] {"symbol", "timestamp", "open"};
    private static final String EXPECTED_CREATE_TABLE_SQL =
            "CREATE TABLE testentity(symbol TEXT,timestamp TEXT,open REAL)";

    IEntityMetaData<?> metaData;


    @Test
    public void shouldGetCorrectTableName() {
        // given
        metaData = EntityMetaDataFactory.get(TestEntity.class);
        // when and then
        assertThat(metaData.getTableName(), is(EXPECTED_TABLE_NAME));
    }

    @Test
    public void shouldGetCorrectColumns() {
        // given
        metaData = EntityMetaDataFactory.get(TestEntity.class);
        // when and then
        assertThat(metaData.getColumns(), is(EXPECTED_COLUMNS));
    }

    @Test
    public void shouldGetCorrectCreateTableSql() {
        // given
        metaData = EntityMetaDataFactory.get(TestEntity.class);
        // when and then
        assertThat(metaData.getCreateTableSql(), is(EXPECTED_CREATE_TABLE_SQL));
    }

    @Test(expected=InvalidAnnotationException.class)
    public void shouldThrowExceptionWhenNoEntityAnnotation() {
        // given
        metaData = EntityMetaDataFactory.get(TestEntityNoEntityAnnotation.class);
        // when
        metaData.getCreateTableSql();

        // then throws exception
    }

    @Test(expected=InvalidAnnotationException.class)
    public void shouldThrowExceptionWhenNoTableAnnotation() {
        // given
        metaData = EntityMetaDataFactory.get(TestEntityNoTableAnnontation.class);
        // when
        metaData.getTableName();

        // then throws exception
    }

    @Test(expected=InvalidAnnotationException.class)
    public void shouldThrowExceptionWhenNoColumnAnnotation() {
        // given
        metaData = EntityMetaDataFactory.get(TestEntityNoColumnAnnotation.class);
        // when
        metaData.getColumns();

        // then throws exception
    }

    @Entity
    @Table(name="testentity")
    private static class TestEntity {

        @Column(name="symbol", columnDefinition="TEXT")
        public String symbol;

        @Column(name="timestamp", columnDefinition="TEXT")
        public Timestamp timestamp;

        @Column(name="open", columnDefinition="REAL")
        public Double open;
    }

    @Table(name="testentity")
    private static class TestEntityNoEntityAnnotation {

        @Column(name="symbol", columnDefinition="TEXT")
        public String symbol;

        @Column(name="timestamp", columnDefinition="TEXT")
        public Timestamp timestamp;

        @Column(name="open", columnDefinition="REAL")
        public Double open;
    }

    @Entity
    private static class TestEntityNoTableAnnontation {

        @Column(name="symbol", columnDefinition="TEXT")
        public String symbol;

        @Column(name="timestamp", columnDefinition="TEXT")
        public Timestamp timestamp;

        @Column(name="open", columnDefinition="REAL")
        public Double open;
    }

    @Entity
    @Table(name="testentity")
    @SuppressWarnings("unused")
    private static class TestEntityNoColumnAnnotation {

        @Column(name="symbol", columnDefinition="TEXT")
        public String symbol;

        public Timestamp timestamp;

        @Column(name="open", columnDefinition="REAL")
        public Double open;
    }

}
