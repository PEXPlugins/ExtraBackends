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
package ninja.leaping.permissionsex.extrabackends;

import ninja.leaping.permissionsex.backend.AbstractDataStore;
import ninja.leaping.permissionsex.backend.DataStore;
import ninja.leaping.permissionsex.data.ContextInheritance;
import ninja.leaping.permissionsex.data.ImmutableSubjectData;
import ninja.leaping.permissionsex.rank.RankLadder;
import ninja.leaping.permissionsex.util.Util;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * A specialization of AbstractDataStore that handles backends for a global data store
 */
public abstract class ReadOnlyDataStore extends AbstractDataStore {

    protected ReadOnlyDataStore(Factory factory) {
        super(factory);
    }

    @Override
    protected CompletableFuture<ImmutableSubjectData> setDataInternal(String type, String identifier, ImmutableSubjectData data) {
        return Util.failedFuture(new UnsupportedOperationException("The " + getClass().getSimpleName() + " backend is-read-only!"));
    }

    @Override
    protected CompletableFuture<RankLadder> setRankLadderInternal(String ladder, RankLadder newLadder) {
        return Util.failedFuture(new UnsupportedOperationException("The " + getClass().getSimpleName() + " backend is-read-only!"));
    }

    @Override
    protected CompletableFuture<ContextInheritance> setContextInheritanceInternal(ContextInheritance contextInheritance) {
        return Util.failedFuture(new UnsupportedOperationException("The " + getClass().getSimpleName() + " backend is-read-only!"));
    }

    @Override
    protected <T> T performBulkOperationSync(Function<DataStore, T> function) throws Exception {
        return function.apply(this);
    }
}
