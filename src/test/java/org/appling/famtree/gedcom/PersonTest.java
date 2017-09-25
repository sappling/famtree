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

import org.gedcom4j.model.Individual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by sappling on 9/24/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class PersonTest {

    @Test
    public void testParseName() {
        Individual i = mock(Individual.class);
        Person p = new Person(i);
        p.parseName("John Hubert /Smith/");
        assertThat(p.getFullName(), equalTo("John Hubert Smith"));
        assertThat(p.getSurname(), equalTo("Smith"));
        assertThat(p.getStartingNames(), equalTo("John Hubert"));
        assertThat(p.getSuffix(), equalTo(""));

        p = new Person(i);
        p.parseName("Dr. John Hubert /Smith/ Sr.");
        assertThat(p.getFullName(), equalTo("Dr. John Hubert Smith Sr."));
        assertThat(p.getSurname(), equalTo("Smith"));
        assertThat(p.getStartingNames(), equalTo("Dr. John Hubert"));
        assertThat(p.getSuffix(), equalTo("Sr."));

    }
}
