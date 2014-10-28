/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portal.layout.profile.dao.jpa;

import org.apache.commons.lang3.Validate;
import org.jasig.portal.jpa.BasePortalJpaDao;
import org.jasig.portal.jpa.OpenEntityManager;
import org.jasig.portal.layout.profile.IProfileSelection;
import org.jasig.portal.layout.profile.dao.IProfileSelectionDao;

/**
 * JPA implementation of IProfilePreferenceDao.
 */
// NOT @Repository so that adopters can decide whether to instantiate through context XML declaration or not.
public class ProfileSelectionDaoImpl
    extends BasePortalJpaDao
    implements IProfileSelectionDao {


    @Override
    @PortalTransactional
    public IProfileSelection createProfileSelection(final String userName, final String profileFName) {

        Validate.notEmpty(userName, "Cannot create a profile selection for an empty userName");
        Validate.notEmpty(profileFName,
                "Cannot create profile selection with empty profile fname " +
                        "(instead delete any selection for this user.)");

        final ProfileSelection jpaProfileSelection = new ProfileSelection(userName, profileFName);

        getEntityManager().persist(jpaProfileSelection);

        return jpaProfileSelection;
    }

    @Override
    @PortalTransactional
    public IProfileSelection updateProfileSelection(final IProfileSelection profileSelection) {

        Validate.notNull(profileSelection);

        getEntityManager().persist(profileSelection);

        return profileSelection;
    }

    @Override
    @PortalTransactional
    public void deleteProfileSelection(final IProfileSelection profileSelection) {

        Validate.notNull(profileSelection, "Cannot delete a null profileSelection.");

        final IProfileSelection persistentProfileSelection;

        if (getEntityManager().contains(profileSelection)) {
            persistentProfileSelection = profileSelection;
        } else {
            persistentProfileSelection = getEntityManager().merge(profileSelection);
        }

        getEntityManager().remove(persistentProfileSelection);

    }

    @Override
    @OpenEntityManager(unitName = PERSISTENCE_UNIT_NAME)
    public IProfileSelection readProfileSelectionForUser(final String userName) {

        final NaturalIdQuery<ProfileSelection> query = createNaturalIdQuery(ProfileSelection.class);
        query.using(ProfileSelection_.userName, userName);

        return query.load();
    }
}
