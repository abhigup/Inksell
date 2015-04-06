package models;

/**
 * Created by Abhinav on 05/04/15.
 */
public class CompanyEntity {

    public int companyId;

    public String companyName;

    public String companyDomain;

    @Override
    public String toString() {
        return this.companyName;            // What to display in the Spinner list.
    }
}
