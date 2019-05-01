package iti.kukumo.db.test.loader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;

import iti.kukumo.db.dataset.DataSet;
import iti.kukumo.db.dataset.OoxmlDataSet;

public class TestOoxmlDataSet {

    @Test
    public void testLoadXLS() throws InvalidFormatException, IOException {
        
        File file = new File("src/test/resources/data1.xlsx");
        try( OoxmlDataSet multiDataSet = new OoxmlDataSet(file,"#.*","<null>")) {
            Iterator<DataSet> iterator = multiDataSet.iterator();
            
            DataSet users = iterator.next();
            assertThat(users.collectColumns(",")).isEqualTo("id,first_name,second_name,active,birth_date");
            assertRow(users, 1., "John", "Smith", true, "2000-10-30");
            assertRow(users, 2., "Annie", "Hall", false, "2011-09-12");
            assertRow(users, 3., "Bruce", null, true, "1982-12-31");
            assertThat(users.nextRow()).isFalse();
            
            DataSet city = iterator.next();
            assertThat(city.collectColumns(",")).isEqualTo("id,name,zip_code");
            assertRow(city, 1., "New York", 46018.);
            assertRow(city, 2., "Baltimore", 55583.);
            assertRow(city, 3., "Tordesillas", 12356.);
            assertThat(city.nextRow()).isFalse();
            
            DataSet userCity = iterator.next();
            assertThat(userCity.collectColumns(",")).isEqualTo("user_id,city_id");
            assertRow(userCity, 1., 1.);
            assertRow(userCity, 1., 3.);
            assertRow(userCity, 2., 1.);
            assertRow(userCity, 2., 2.);
            assertRow(userCity, 3., 3.);
            assertThat(userCity.nextRow()).isFalse();

            assertThat(iterator.hasNext()).isFalse();
        }
        
    }

    
    
    private void assertRow(DataSet dataSet, Object... values) {
        assertThat(dataSet.nextRow()).isTrue();
        for (int i=0;i<values.length;i++) {
            if (values[i] == null) {
                assertThat(dataSet.rowValue(i)).isNull();
            } else {
                assertThat(dataSet.rowValue(i)).isExactlyInstanceOf(values[i].getClass());
                assertThat(dataSet.rowValue(i)).isEqualTo(values[i]);
            }
        }
        
    }
}
