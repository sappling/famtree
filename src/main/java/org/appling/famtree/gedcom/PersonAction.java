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

/**
 * Created by sappling on 8/15/2017.
 */
public interface PersonAction {
    /**
     * Perform the action needed for each person when walking the tree.
     * @param person - the current person in the walk
     * @param spouseCount - 0=descendant, 1=first spouse, 2=second spouse, etc.
     * @param generation - 0 = generation at the root of the tree
     */
    void act(Person person, int spouseCount, int generation);

    /**
     * Action needed for the last child or last spouse of last child.
     * @param person
     */
    void lastChildOrSpouse(Person person);
}
