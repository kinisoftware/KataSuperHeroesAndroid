/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.katasuperheroes.model;

public class SuperHero {

    private final String name;
    private final String photo;
    private final boolean isAvenger;
    private final String description;

    public SuperHero(String name, String photo, boolean isAvenger, String description) {
        this.name = name;
        this.photo = photo;
        this.isAvenger = isAvenger;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public boolean isAvenger() {
        return isAvenger;
    }

    public String getDescription() {
        return description;
    }

    public static class Builder {
        private String name;
        private String photo;
        private boolean isAvenger;
        private String description;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withPhoto(String photo) {
            this.photo = photo;
            return this;
        }

        public Builder isAvanger(boolean isAvenger) {
            this.isAvenger = isAvenger;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public SuperHero build() {
            return new SuperHero(name, photo, isAvenger, description);
        }
    }
}
