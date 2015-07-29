/**
 * PermissionsEx
 * Copyright (C) zml and PermissionsEx contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.leaping.permissionsex.extrabackends.groupmanager;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.permissionsex.backend.DataStore;
import ninja.leaping.permissionsex.data.ContextInheritance;
import ninja.leaping.permissionsex.data.ImmutableOptionSubjectData;
import ninja.leaping.permissionsex.exception.PermissionsLoadingException;
import ninja.leaping.permissionsex.extrabackends.ReadOnlyDataStore;
import ninja.leaping.permissionsex.rank.FixedRankLadder;
import ninja.leaping.permissionsex.rank.RankLadder;

import java.util.Map;
import java.util.Set;

/**
 * Backend implementing GroupManager data storage format
 */
public class GroupManagerDataStore extends ReadOnlyDataStore {
    public static final Factory FACTORY = new Factory("groupmanager", GroupManagerDataStore.class);

    @Setting("group-manager-root")
    private String groupManagerRoot = "plugins/GroupManager";

    protected GroupManagerDataStore() {
        super(FACTORY);
    }

    @Override
    protected void initializeInternal() throws PermissionsLoadingException {

    }

    @Override
    protected ImmutableOptionSubjectData getDataInternal(String type, String identifier) throws PermissionsLoadingException {
        return null;
    }

    @Override
    protected RankLadder getRankLadderInternal(String ladder) {
        return new FixedRankLadder(ladder, ImmutableList.<Map.Entry<String, String>>of()); // GM does not have a concept of rank ladders
    }

    @Override
    protected ContextInheritance getContextInheritanceInternal() {
        return null;
    }

    @Override
    protected <T> T performBulkOperationSync(Function<DataStore, T> function) throws Exception {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isRegistered(String type, String identifier) {
        return false;
    }

    @Override
    public Iterable<String> getAllIdentifiers(String type) {
        return null;
    }

    @Override
    public Set<String> getRegisteredTypes() {
        return null;
    }

    @Override
    public Iterable<Map.Entry<Map.Entry<String, String>, ImmutableOptionSubjectData>> getAll() {
        return null;
    }

    @Override
    public Iterable<String> getAllRankLadders() {
        return null;
    }

    @Override
    public boolean hasRankLadder(String ladder) {
        return false;
    }
}
