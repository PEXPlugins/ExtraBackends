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

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import ninja.leaping.permissionsex.backend.AbstractDataStore;
import ninja.leaping.permissionsex.backend.DataStore;
import ninja.leaping.permissionsex.data.ContextInheritance;
import ninja.leaping.permissionsex.data.ImmutableOptionSubjectData;
import ninja.leaping.permissionsex.rank.RankLadder;

/**
 * A specialization of AbstractDataStore that handles backends for a global data store
 */
public abstract class ReadOnlyDataStore extends AbstractDataStore {

    protected ReadOnlyDataStore(Factory factory) {
        super(factory);
    }

    @Override
    protected ListenableFuture<ImmutableOptionSubjectData> setDataInternal(String type, String identifier, ImmutableOptionSubjectData data) {
        return Futures.immediateFailedFuture(new UnsupportedOperationException("The " + getClass().getSimpleName() + " backend is-read-only!"));
    }

    @Override
    protected ListenableFuture<RankLadder> setRankLadderInternal(String ladder, RankLadder newLadder) {
        return Futures.immediateFailedFuture(new UnsupportedOperationException("The " + getClass().getSimpleName() + " backend is-read-only!"));
    }

    @Override
    protected ListenableFuture<ContextInheritance> setContextInheritanceInternal(ContextInheritance contextInheritance) {
        return Futures.immediateFailedFuture(new UnsupportedOperationException("The " + getClass().getSimpleName() + " backend is-read-only!"));
    }

    @Override
    protected <T> T performBulkOperationSync(Function<DataStore, T> function) throws Exception {
        return function.apply(this);
    }
}
