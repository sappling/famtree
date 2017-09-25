/*
 * Copyright (c) 2017 Steve Appling
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.appling.famtree.gedcom;

import org.appling.famtree.graph.PersonFrame;
import org.appling.famtree.util.DateUtils;
import org.gedcom4j.model.*;
import org.gedcom4j.model.enumerations.IndividualEventType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sappling on 8/12/2017.
 */
public class Person {
    private static Pattern namePattern = Pattern.compile("(.+)\\s+/(.+)/\\s*(\\S+)*");

    private final Individual individual;
    private PersonFrame frame = null;
    private String fullName = "";
    private String surname = "";
    private String suffix = "";
    private String startingNames = "";

    public Person(Individual individual) {
        this.individual = individual;
        parseAllNames();
    }

    @NotNull
    public String getGedcomId() {
        return individual.getXref();
    }

    public String getCleanId() {
        return getCleanId(getGedcomId());
    }

    public static String getCleanId(String gedcomId) {
        String result = gedcomId;
        if (result.startsWith("@") && result.endsWith("@")) {
            result = result.substring(1, result.length()-1);
        }
        return result;
    }

    public String getSurname() {
        return surname;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getStartingNames() {
        return startingNames;
    }

    public PersonFrame getFrame() {
        return frame;
    }

    public void setFrame(PersonFrame frame) {
        this.frame = frame;
    }

    private void parseAllNames() {
        List<PersonalName> names = individual.getNames();
        if (names.size() > 0) {
            PersonalName name = names.get(0);
            parseName(name.getBasic());
        }

    }

    protected void parseName(String name) {
        // This name is in the form "title first middle /last/ suffix"
        Matcher matcher = namePattern.matcher(name);
        if (matcher.matches()) {
            startingNames = matcher.group(1);
            surname = matcher.group(2);
            String group3 = matcher.group(3);
            if (group3 != null) {
                suffix = group3;
            }
        }

        fullName = name.replaceAll("/","");
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
        if (familiesWhereChild != null && !familiesWhereChild.isEmpty()) {
            IndividualReference husband = familiesWhereChild.get(0).getFamily().getHusband();
            if (husband != null) {
                result = PersonRegistry.instance().getPerson(Person.getCleanId(husband.getIndividual().getXref()));
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
                result = PersonRegistry.instance().getPerson(Person.getCleanId(wife.getIndividual().getXref()));
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return getCleanId().equals(person.getCleanId());
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
        return getCleanId().hashCode();
    }

    public String toString() {
        return getFullName() + " : "+ getCleanId();
    }
}
