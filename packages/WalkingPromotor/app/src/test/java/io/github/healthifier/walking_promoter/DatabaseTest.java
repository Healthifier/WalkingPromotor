package io.github.healthifier.walking_promoter;

import com.google.common.collect.Lists;

import io.github.healthifier.walking_promoter.models.Database;
import io.github.healthifier.walking_promoter.models.SquareStepRecord;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@RunWith(RobolectricTestRunner.class)
public class DatabaseTest {
    private Database _db;

    @Before
    public void setUp() throws Exception {
        _db = new Database(RuntimeEnvironment.application);
        _db.truncateAll();
    }

    @After
    public void tearDown() throws Exception {
        _db.close();
    }

    @Test
    public void testUserId() {
        assertThat(_db.getUserId(), is(""));

        _db.setUserId("HOGE");
        assertThat(_db.getUserId(), is("HOGE"));

        _db.setUserId("PIYO");
        assertThat(_db.getUserId(), is("PIYO"));
    }

    @Test
    public void testSquareStepRecord() {
        assertThat(_db.getSquareStepRecord(0), is(nullValue()));
        assertThat(_db.getSquareStepRecord(1), is(nullValue()));
        assertThat(_db.getSquareStepRecord(2), is(nullValue()));

        _db.setSquareStepRecord(new SquareStepRecord(2, false, 10, 2));

        assertThat(_db.getSquareStepRecord(0), is(nullValue()));
        assertThat(_db.getSquareStepRecord(1), is(nullValue()));
        assertThat(_db.getSquareStepRecord(2), is(equalTo(new SquareStepRecord(2, false, 10, 2))));

        _db.setSquareStepRecord(new SquareStepRecord(1, true, 20, 200));

        assertThat(_db.getSquareStepRecord(0), is(nullValue()));
        assertThat(_db.getSquareStepRecord(1), is(equalTo(new SquareStepRecord(1, true, 20, 200))));
        assertThat(_db.getSquareStepRecord(2), is(equalTo(new SquareStepRecord(2, false, 10, 2))));

        _db.setSquareStepRecord(new SquareStepRecord(2, true, 30, 300));

        assertThat(_db.getSquareStepRecord(0), is(nullValue()));
        assertThat(_db.getSquareStepRecord(1), is(equalTo(new SquareStepRecord(1, true, 20, 200))));
        assertThat(_db.getSquareStepRecord(2), is(equalTo(new SquareStepRecord(2, true, 30, 300))));
    }

    @Test
    public void testSquareStepIds() {
        assertThat(_db.getSquareStepIds().size(), is(7));

        _db.setSquareStepIds("0");
        assertThat(_db.getSquareStepIds().toArray(), equalTo(Lists.newArrayList(0).toArray()));

        _db.setSquareStepIds("1\t57");
        assertThat(_db.getSquareStepIds().toArray(), equalTo(Lists.newArrayList(1, 57).toArray()));

        _db.setSquareStepIds("aa");
        assertThat(_db.getSquareStepIds().toArray(), equalTo(Lists.newArrayList(1, 57).toArray()));
    }
}
