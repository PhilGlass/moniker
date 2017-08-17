package glass.phil.monzo.model;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.CLASS;

@Qualifier
@Retention(CLASS)
public @interface Authorized {}
