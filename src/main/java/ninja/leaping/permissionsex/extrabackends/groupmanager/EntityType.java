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

import ninja.leaping.configurate.ConfigurationNode;

import java.util.Map;

public enum  EntityType {
    USER {
        @Override
        public ConfigurationNode getGlobalNode(GroupManagerDataStore dataStore) {
            return null;
        }

        @Override
        public ConfigurationNode getWorldNode(GroupManagerDataStore dataStore, String world) {
            Map.Entry<ConfigurationNode, ConfigurationNode> worldPair = dataStore.getUserGroupsConfigForWorld(world);
            if (worldPair == null) {
                return null;
            }
            return worldPair.getKey();
        }

        @Override
        public ConfigurationNode getNodeForSubject(ConfigurationNode root, String name) {
            return root.getNode("users", name);
        }

        @Override
        public String getInheritanceKey() {
            return "group";
        }
    },
    GROUP {
        @Override
        public ConfigurationNode getGlobalNode(GroupManagerDataStore dataStore) {
            return dataStore.getGlobalGroups();
        }

        @Override
        public ConfigurationNode getWorldNode(GroupManagerDataStore dataStore, String world) {
            Map.Entry<ConfigurationNode, ConfigurationNode> worldPair = dataStore.getUserGroupsConfigForWorld(world);
            if (worldPair == null) {
                return null;
            }
            return worldPair.getValue();
        }

        @Override
        public ConfigurationNode getNodeForSubject(ConfigurationNode root, String name) {
            ConfigurationNode ret = root.getNode("groups", name);
            if (ret.isVirtual()) {
                ConfigurationNode global = root.getNode("groups", "g:" + name);
                if (!global.isVirtual()) {
                    return global;
                }
            }
            return ret;
        }
    },
    OTHER {
        @Override
        public ConfigurationNode getGlobalNode(GroupManagerDataStore dataStore) {
            return null;
        }

        @Override
        public ConfigurationNode getWorldNode(GroupManagerDataStore dataStore, String world) {
            return null;
        }

        @Override
        public ConfigurationNode getNodeForSubject(ConfigurationNode root, String name) {
            return null;
        }
    };


    public abstract ConfigurationNode getGlobalNode(GroupManagerDataStore dataStore);

    public abstract ConfigurationNode getWorldNode(GroupManagerDataStore dataStore, String world);

    public abstract ConfigurationNode getNodeForSubject(ConfigurationNode root, String name);

    public String getInheritanceKey() {
        return "inheritance";
    }

    public static EntityType forTypeString(String type) {
        if (type.equals("user")) {
            return USER;
        } else if (type.equals("group")) {
            return GROUP;
        } else {
            return OTHER;
        }
    }
}
