package glass.phil.monzo.model.transactions;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.CLASS;

@Qualifier
@Retention(CLASS)
public @interface DbName {}
