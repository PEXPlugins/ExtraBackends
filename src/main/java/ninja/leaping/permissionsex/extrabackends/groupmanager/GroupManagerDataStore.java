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
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import ninja.leaping.permissionsex.data.ContextInheritance;
import ninja.leaping.permissionsex.data.ImmutableSubjectData;
import ninja.leaping.permissionsex.exception.PermissionsLoadingException;
import ninja.leaping.permissionsex.extrabackends.ReadOnlyDataStore;
import ninja.leaping.permissionsex.rank.FixedRankLadder;
import ninja.leaping.permissionsex.rank.RankLadder;
import org.yaml.snakeyaml.DumperOptions;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ninja.leaping.permissionsex.util.Translations._;

/**
 * Backend implementing GroupManager data storage format
 */
public class GroupManagerDataStore extends ReadOnlyDataStore {
    public static final Factory FACTORY = new Factory("groupmanager", GroupManagerDataStore.class);

    @Setting("group-manager-root")
    private String groupManagerRoot = "plugins/GroupManager";

    private ConfigurationNode config;
    private ConfigurationNode globalGroups;
    private Map<String, Map.Entry<ConfigurationNode, ConfigurationNode>> worldUserGroups;
    private GroupManagerContextInheritance contextInheritance;

    protected GroupManagerDataStore() {
        super(FACTORY);
    }

    private ConfigurationLoader<ConfigurationNode> getLoader(File file) {
        return YAMLConfigurationLoader.builder()
                .setFlowStyle(DumperOptions.FlowStyle.BLOCK)
                .setFile(file)
                .build();
    }

    ConfigurationNode getGlobalGroups() {
        return globalGroups;
    }

    Map.Entry<ConfigurationNode, ConfigurationNode> getUserGroupsConfigForWorld(String world) {
        return worldUserGroups.get(world);
    }

    @Override
    protected void initializeInternal() throws PermissionsLoadingException {
        final File rootFile = new File(groupManagerRoot);
        if (!rootFile.isDirectory()) {
            throw new PermissionsLoadingException(_("GroupManager directory %s does not exist", rootFile)); // TODO: Actual translations
        }
        try {
            config = getLoader(new File(rootFile, "config.yml")).load();
            globalGroups = getLoader(new File(rootFile, "globalgroups.yml")).load();
            worldUserGroups = new HashMap<>();
            for (File world : new File(rootFile, "worlds").listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            }))  {
                worldUserGroups.put(world.getName(), Maps.immutableEntry(
                        getLoader(new File(world, "users.yml")).load(),
                        getLoader(new File(world, "groups.yml")).load()
                ));
            }
            contextInheritance = new GroupManagerContextInheritance(config.getNode("settings", "mirrors"));
        } catch (IOException e) {
            throw new PermissionsLoadingException(e);
        }

    }

    @Override
    protected ImmutableSubjectData getDataInternal(String type, String identifier) throws PermissionsLoadingException {
        return new GroupManagerSubjectData(identifier, this, EntityType.forTypeString(type));
    }

    @Override
    protected RankLadder getRankLadderInternal(String ladder) {
        return new FixedRankLadder(ladder, ImmutableList.<Map.Entry<String, String>>of()); // GM does not have a concept of rank ladders
    }

    @Override
    protected ContextInheritance getContextInheritanceInternal() {
        return contextInheritance;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isRegistered(String type, String identifier) {
        if (type.equals("user")) {
            for (Map.Entry<String, Map.Entry<ConfigurationNode, ConfigurationNode>> ent : this.worldUserGroups.entrySet()) {
                if (!ent.getValue().getKey().getNode("users", identifier).isVirtual()) {
                    return true;
                }

            }
        } else if (type.equals("group")) {
            if (!globalGroups.getNode("groups", "g:" + identifier).isVirtual()) {
                return true;
            }
            for (Map.Entry<String, Map.Entry<ConfigurationNode, ConfigurationNode>> ent : this.worldUserGroups.entrySet()) {
                if (!ent.getValue().getValue().getNode("groups", identifier).isVirtual()) {
                    return true;
                }

            }

        }
        return false;
    }

    @Override
    public Iterable<String> getAllIdentifiers(String type) {
        if (type.equals("user")) {
            return Sets.newHashSet(Iterables.transform(Iterables.concat(Iterables.transform(this.worldUserGroups.values(), new Function<Map.Entry<ConfigurationNode, ConfigurationNode>, Set<Object>>() {
                @Nullable
                @Override
                public Set<Object> apply(@Nullable Map.Entry<ConfigurationNode, ConfigurationNode> input) {
                    return input.getKey().getNode("users").getChildrenMap().keySet();
                }
            })), Functions.toStringFunction()));
        } else if (type.equals("group")) {
            return Sets.newHashSet(Iterables.transform(Iterables.concat(Iterables.concat(Iterables.transform(this.worldUserGroups.values(), new Function<Map.Entry<ConfigurationNode, ConfigurationNode>, Set<Object>>() {
                @Nullable
                @Override
                public Set<Object> apply(@Nullable Map.Entry<ConfigurationNode, ConfigurationNode> input) {
                    return input.getValue().getNode("groups").getChildrenMap().keySet();
                }
            })), Iterables.transform(globalGroups.getNode("groups").getChildrenMap().keySet(), new Function<Object, Object>() {
                @Nullable
                @Override
                public Object apply(@Nullable Object input) {
                    if (input instanceof String && ((String) input).startsWith("g:")) {
                        input = ((String) input).substring(2);
                    }
                    return input;
                }
            })), Functions.toStringFunction()));
        } else {
            return ImmutableSet.of();
        }
    }

    @Override
    public Set<String> getRegisteredTypes() {
        return ImmutableSet.of("user", "group");
    }

    @Override
    public Iterable<Map.Entry<Map.Entry<String, String>, ImmutableSubjectData>> getAll() {
        return Iterables.transform(Iterables.concat(Iterables.transform(getAllIdentifiers("user"), nameToSubjectForType("user")),
                Iterables.transform(getAllIdentifiers("group"), nameToSubjectForType("group"))), new Function<Map.Entry<String, String>, Map.Entry<Map.Entry<String, String>, ImmutableSubjectData>>() {
            @Nullable
            @Override
            public Map.Entry<Map.Entry<String, String>, ImmutableSubjectData> apply(@Nullable Map.Entry<String, String> input) {
                return Maps.immutableEntry(input, getData(input.getKey(), input.getValue(), null));
            }
        });
    }

    private static Function<String, Map.Entry<String, String>> nameToSubjectForType(final String type) {
        return new Function<String, Map.Entry<String, String>>() {
            @Nullable
            @Override
            public Map.Entry<String, String> apply(String input) {
                return Maps.immutableEntry(type, input);
            }
        };
    }

    @Override
    public Iterable<String> getAllRankLadders() {
        return ImmutableList.of();
    }

    @Override
    public boolean hasRankLadder(String ladder) {
        return false;
    }

    public Collection<String> getKnownWorlds() {
        return this.worldUserGroups.keySet();
    }
}
