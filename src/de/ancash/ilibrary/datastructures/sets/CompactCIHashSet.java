package de.ancash.ilibrary.datastructures.sets;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import de.ancash.ilibrary.datastructures.maps.CaseInsensitiveMap;

/**
 * Similar to CompactSet, except that it uses a HashSet as delegate Set when
 * more than compactSize() elements are held.
 *
 * @author John DeRegnaucourt (jdereg@gmail.com)
 *         <br>
 *         Copyright (c) Cedar Software LLC
 *         <br><br>
 *         Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <br><br>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         <br><br>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 */
public class CompactCIHashSet<E> extends CompactSet<E>
{
    public CompactCIHashSet() { }
    public CompactCIHashSet(Collection<E> other) { super(other); }

    /**
     * @return new empty Set instance to use when size() becomes {@literal >} compactSize().
     */
    protected Set<E> getNewSet() { return new CaseInsensitiveSet<>(Collections.emptySet(), new CaseInsensitiveMap<>(Collections.emptyMap(), new HashMap<>(compactSize() + 1))); }
    protected boolean isCaseInsensitive() { return true; }
}
