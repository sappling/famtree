package org.appling.famtree.gedcom;

import org.appling.famtree.graph.PersonFrame;
import org.appling.famtree.util.DateUtils;
import org.gedcom4j.model.*;
import org.gedcom4j.model.enumerations.IndividualEventType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by sappling on 8/12/2017.
 */
public class Person {

    private final Individual individual;
    private PersonFrame frame = null;
    private String fullName = "";

    public Person(Individual individual) {
        this.individual = individual;
        parseName();
    }

    @NotNull
    public String getId() {
        return individual.getXref();
    }

    public String getCleanId() {
        String result = getId();
        if (result.startsWith("@") && result.endsWith("@")) {
            result = result.substring(1, result.length()-2);
        }
        return result;
    }

    public PersonFrame getFrame() {
        return frame;
    }

    public void setFrame(PersonFrame frame) {
        this.frame = frame;
    }

    private void parseName() {
        List<PersonalName> names = individual.getNames();
        if (names.size() > 0) {
            PersonalName name = names.get(0);
            String basicName = name.getBasic();
            fullName = basicName.replaceAll("/","");

            // This name is in the form "first middle /last/"

            //todo - need to use regex instead.  Live with for now while exploring graphic part
            //Pattern p = Pattern.compile("(\\S+)\s()");

            /*
            String[] splits = basicName.split(" ");
            if (splits.length > 0) {
                firstName = splits[0];
            } if (splits.length > 1) {
                lastName = splits[splits.length-1];
            } if (splits.length > 2) {
                String[] middle = Arrays.copyOfRange(splits, 1, splits.length - 2);
                for (int i=0; i<middle.length; i++) {
                    middleNames += middle[i];
                    if (i<middle.length) {
                        middleNames += " ";
                    }
                }
            }
            */
        }

    }

    public String getFullName() {

        return fullName;
    }

    /**
     * Return the path to the profile image or null if no profile image defined.
     * Note that profile images are unique to Family Tree Maker GEDCOM format
     * and use a custom _PHOTO tag.  These are not available in GEDCOM information
     * exported from ancestry directly.
     * @return
     */
    @Nullable
    public String getProfileImagePath() {
        String result = null;
        String photoRef = null;
        List<CustomFact> photoList = individual.getCustomFactsWithTag("_PHOTO");
        if (!photoList.isEmpty()) {
            CustomFact photoFact = photoList.get(0);
            photoRef = photoFact.getDescription().getValue();
        }
        if (photoRef != null) {
            List<MultimediaReference> multimedia = individual.getMultimedia();
            for (MultimediaReference reference : multimedia) {
                Multimedia media = reference.getMultimedia();
                if (media.getXref().equals(photoRef)) {
                    List<FileReference> fileReferences = media.getFileReferences();
                    if (!fileReferences.isEmpty()) {
                        result = fileReferences.get(0).getReferenceToFile().getValue();
                    }
                }
            }
        }
        return result;
    }


    public List<Family> getFamiliesWhereSpouse() {
        List<FamilySpouse> familiesWhereSpouse = individual.getFamiliesWhereSpouse();
        ArrayList<Family> result = new ArrayList<>();

        if (familiesWhereSpouse != null) {
            for (FamilySpouse familySpouse : familiesWhereSpouse) {
                result.add(new Family(familySpouse.getFamily()));
            }
        }
        return result;
    }

    public Person getFather() throws GedException {
        Person result = null;
        List<FamilyChild> familiesWhereChild = individual.getFamiliesWhereChild();
        if (!familiesWhereChild.isEmpty()) {
            IndividualReference husband = familiesWhereChild.get(0).getFamily().getHusband();
            if (husband != null) {
                result = PersonRegistry.instance().getPerson(husband.getIndividual().getXref());
            }
        }
        return result;
    }

    public Person getMother() throws GedException {
        Person result = null;
        List<FamilyChild> familiesWhereChild = individual.getFamiliesWhereChild();
        if (!familiesWhereChild.isEmpty()) {
            IndividualReference wife = familiesWhereChild.get(0).getFamily().getWife();
            if (wife != null) {
                result = PersonRegistry.instance().getPerson(wife.getIndividual().getXref());
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return getId().equals(person.getId());
    }

    @Nullable
    private String getRawBirthString() {
        String result = null;
        List<IndividualEvent> births = individual.getEventsOfType(IndividualEventType.BIRTH);

        if (!births.isEmpty()) {
            StringWithCustomFacts date = births.get(0).getDate();
            if (date != null) {
                result = date.getValue();
            }
        }
        return result;
    }

    @NotNull
    public String getBirthString() {
        String result = getRawBirthString();
        if (result == null) {
            result = "Unknown";
        }

        return result;
    }

    /**
     * Gets the birth date
     * @return birth date or Null if unknown
     */
    @Nullable
    public Date getBirthDate() {
        return DateUtils.dateFromGedDate(getRawBirthString());
    }

    @Nullable
    private String getRawDeathString() {
        String result = null;
        List<IndividualEvent> deaths = individual.getEventsOfType(IndividualEventType.DEATH);

        if (!deaths.isEmpty()) {
            StringWithCustomFacts date = deaths.get(0).getDate();
            if (date != null) {
                result = date.getValue();
            }
        }
        return result;
    }

    @NotNull
    public String getDeathString() {
        String result = getRawDeathString();
        if (result == null) {
            result = "Unknown";
        }

        return result;
    }

    /**
     * Gets the death date
     * @return death date or Null if unknown
     */
    @Nullable
    public Date getDeathDate() {
        return DateUtils.dateFromGedDate(getRawDeathString());
    }

    @Override
    public int hashCode() {
        return individual.getXref().hashCode();
    }

    public String toString() {
        return getFullName() + " : "+ getId();
    }
}
