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

package com.karumi.katasuperheroes;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.recyclerview.RecyclerViewInteraction;
import com.karumi.katasuperheroes.ui.view.MainActivity;
import com.karumi.katasuperheroes.ui.view.SuperHeroDetailActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public DaggerMockRule<MainComponent> daggerRule =
            new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
                    new DaggerMockRule.ComponentSetter<MainComponent>() {
                        @Override
                        public void setComponent(MainComponent component) {
                            SuperHeroesApplication app =
                                    (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                                            .getTargetContext()
                                            .getApplicationContext();
                            app.setComponent(component);
                        }
                    });

    @Rule
    public IntentsTestRule<MainActivity> activityRule =
            new IntentsTestRule<>(MainActivity.class, true, false);

    @Mock
    SuperHeroesRepository repository;

    @Test
    public void showsEmptyCaseIfThereAreNoSuperHeroes() {
        givenThereAreNoSuperHeroes();

        startActivity();

        onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()));
    }

    @Test
    public void shouldNotShowEmptyCaseIfThereAreSomeSuperHeroes() {
        givenThereAreSomeSuperHeroes(1);

        startActivity();

        onView(withText("¯\\_(ツ)_/¯")).check(matches(not(isDisplayed())));
    }

    @Test
    public void showsSuperHeroeNameWhenThereIsOneSuperHeroes() {
        SuperHero superHero = new SuperHero.Builder()
                .withName("Super Perrete")
                .build();
        givenThereAreSomeSuperHeroes(superHero);

        startActivity();

        onView(withText(superHero.getName())).check(matches(isDisplayed()));
    }

    @Test
    public void showsEachSuperHeroNameWhenThereAreTenSuperHeroes() {
        List<SuperHero> superHeroes = givenThereAreSomeSuperHeroes(10);

        startActivity();

        RecyclerViewInteraction.<SuperHero>onRecyclerView(withId(R.id.recycler_view))
                .withItems(superHeroes)
                .check(new RecyclerViewInteraction.ItemViewAssertion<SuperHero>() {
                    @Override
                    public void check(SuperHero superHero, View view, NoMatchingViewException e) {
                        matches(hasDescendant(withText(superHero.getName()))).check(view, e);
                    }
                });
    }

    @Test
    public void doesNotShowAvengerBadgeWhenThereIsNotAvengers() throws Exception {
        List<SuperHero> superHeroes = givenThereAreSomeSuperHeroesNoAvengers(10);

        startActivity();

        RecyclerViewInteraction.<SuperHero>onRecyclerView(withId(R.id.recycler_view))
                .withItems(superHeroes)
                .check(new RecyclerViewInteraction.ItemViewAssertion<SuperHero>() {
                    @Override
                    public void check(SuperHero superHero, View view, NoMatchingViewException e) {
                        assertTrue(view.findViewById(R.id.iv_avengers_badge).getVisibility() != View.VISIBLE);
                        // match all off with descendant
                    }
                });
    }

    @Test
    public void navigationToSuperHeroDetailActivityIsDoneWhenASuperHeroIsClicked() {
        List<SuperHero> superHeroes = givenThereAreSomeSuperHeroes(2);

        startActivity();
        andClickASuperHero(0);

        SuperHero selectedSuperHero = superHeroes.get(0);
        intended(hasComponent(SuperHeroDetailActivity.class.getCanonicalName()));
        intended(hasExtra("super_hero_name_key", selectedSuperHero.getName()));
    }

    private List<SuperHero> givenThereAreSomeSuperHeroes(int numberOfSuperHeroes) {
        String[] superHeroNames = {"Fini", "Kina"};

        List<SuperHero> superHeroes = new ArrayList<>();
        for (int i = 0; i < numberOfSuperHeroes; i++) {
            SuperHero superHero = new SuperHero.Builder()
                    .withName(superHeroNames[i % 2])
                    .build();
            superHeroes.add(superHero);
            when(repository.getAll()).thenReturn(superHeroes);
            when(repository.getByName(superHero.getName())).thenReturn(superHero);
        }
        return superHeroes;
    }

    private List<SuperHero> givenThereAreSomeSuperHeroesNoAvengers(int numberOfSuperHeroes) {
        String[] superHeroNames = {"Fini", "Kina"};

        List<SuperHero> superHeroes = new ArrayList<>();
        for (int i = 0; i < numberOfSuperHeroes; i++) {
            SuperHero superHero = new SuperHero.Builder()
                    .withName(superHeroNames[i % 2])
                    .isAvanger(false)
                    .build();
            superHeroes.add(superHero);
            when(repository.getAll()).thenReturn(superHeroes);
            when(repository.getByName(superHero.getName())).thenReturn(superHero);
        }
        return superHeroes;
    }

    private void givenThereAreSomeSuperHeroes(SuperHero... superHeroes) {
        when(repository.getAll()).thenReturn(Arrays.asList(superHeroes));
    }

    private void givenThereAreNoSuperHeroes() {
        when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
    }

    private MainActivity startActivity() {
        return activityRule.launchActivity(null);
    }

    private void andClickASuperHero(int position) {
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }
}