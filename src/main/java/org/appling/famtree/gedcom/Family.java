package org.appling.famtree.gedcom;

import org.appling.famtree.util.PersonBirthdayComparator;
import org.gedcom4j.model.IndividualReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sappling on 8/13/2017.
 */
public class Family {
    private static final PersonBirthdayComparator bCompare = new PersonBirthdayComparator();
    private org.gedcom4j.model.Family gfamily;


    public Family(org.gedcom4j.model.Family gfamily) {
        this.gfamily = gfamily;
    }

    @Nullable
    public Person getOtherSpouse(Person person) throws GedException {

        if (person.equals(getHusband())) {
            return getWife();
        } else if (person.equals(getWife())) {
            return getHusband();
        }
        throw new GedException("Can't find spouce for "+person);
    }

    @Nullable
    public Person getHusband() throws GedException {
        Person result = null;
        IndividualReference husband = gfamily.getHusband();
        if (husband != null) {
            result = PersonRegistry.instance().getPerson(husband.getIndividual().getXref());
        }
        return result;
    }

    public Person getWife() throws GedException {
        Person result = null;
        IndividualReference wife = gfamily.getWife();
        if (wife != null) {
            result = PersonRegistry.instance().getPerson(wife.getIndividual().getXref());
        }
        return result;
    }

    public List<Person> getChildren() throws GedException {
        List<IndividualReference> gChildren = gfamily.getChildren();
        ArrayList<Person> results = new ArrayList<>();

        if (gChildren != null) {
            for (IndividualReference gChild : gChildren) {
                String id = gChild.getIndividual().getXref();
                PersonRegistry pr = PersonRegistry.instance();
                results.add(pr.getPerson(id));
            }
        }

        results.sort(bCompare);

        return results;
    }
}
