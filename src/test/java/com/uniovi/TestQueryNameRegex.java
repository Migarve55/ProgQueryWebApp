package com.uniovi;

import static org.junit.Assert.*;

import org.junit.Test;

import com.uniovi.validators.AddQueryValidator;

public class TestQueryNameRegex {
	
	@Test
	public void testValid() {
		assertTrue("a.b.c".matches(AddQueryValidator.NAME_REGEX));
		assertTrue("abc".matches(AddQueryValidator.NAME_REGEX));
		assertTrue("a.bc".matches(AddQueryValidator.NAME_REGEX));
		assertTrue("a.b.*".matches(AddQueryValidator.NAME_REGEX));
		assertTrue("a.*".matches(AddQueryValidator.NAME_REGEX));
	}
	
	@Test
	public void testInvalid() {
		assertFalse("a.*.c".matches(AddQueryValidator.NAME_REGEX));
		assertFalse(".a.b".matches(AddQueryValidator.NAME_REGEX));
		assertFalse("a.b..".matches(AddQueryValidator.NAME_REGEX));
		assertFalse("*.a".matches(AddQueryValidator.NAME_REGEX));
		assertFalse("a.**".matches(AddQueryValidator.NAME_REGEX));
		assertFalse(".".matches(AddQueryValidator.NAME_REGEX));
		assertFalse("*".matches(AddQueryValidator.NAME_REGEX));
		assertFalse("*.".matches(AddQueryValidator.NAME_REGEX));
		assertFalse(".*".matches(AddQueryValidator.NAME_REGEX));
	}

}
