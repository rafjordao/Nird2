package net.sf.briar.i18n;
import java.awt.Font;
import java.io.File;
import java.util.Locale;

import junit.framework.TestCase;
import net.sf.briar.TestUtils;
import net.sf.briar.api.i18n.FontManager;

import org.junit.Test;

public class FontManagerTest extends TestCase {

	private final File fontDir = TestUtils.getFontDirectory();

	@Test
	public void testBundledFontsAreLoaded() {
		FontManager fontManager = new FontManagerImpl();
		fontManager.initialize(Locale.UK, fontDir);

		Font font = fontManager.getFontForLanguage("en"); // English
		assertEquals(12, font.getSize());

		font = fontManager.getFontForLanguage("bo"); // Tibetan
		assertEquals("Tibetan Machine Uni", font.getFamily());
		assertEquals(14, font.getSize());

		font = fontManager.getFontForLanguage("my"); // Burmese
		assertEquals("Padauk", font.getFamily());
		assertEquals(14, font.getSize());
	}

	@Test
	public void testInternationalCharactersCanBeDisplayed() {
		FontManager fontManager = new FontManagerImpl();
		fontManager.initialize(Locale.UK, fontDir);

		Font font = fontManager.getFontForLanguage("en"); // English
		assertTrue(font.canDisplay('a'));

		font = fontManager.getFontForLanguage("ar"); // Arabic
		assertTrue(font.canDisplay('\u0627')); // An Arabic character

		font = fontManager.getFontForLanguage("bo"); // Tibetan
		assertTrue(font.canDisplay('\u0f00')); // A Tibetan character

		font = fontManager.getFontForLanguage("fa"); // Persian
		assertTrue(font.canDisplay('\ufb56')); // A Persian character

		font = fontManager.getFontForLanguage("my"); // Burmese
		assertTrue(font.canDisplay('\u1000')); // A Burmese character

		font = fontManager.getFontForLanguage("zh"); // Chinese
		assertTrue(font.canDisplay('\u4e00')); // A Chinese character
	}

	@Test
	public void testSetAndGetUiFont() {
		FontManager fontManager = new FontManagerImpl();
		fontManager.initialize(Locale.UK, fontDir);
		Font font = fontManager.getUiFont();
		assertEquals(12, font.getSize());

		fontManager.setUiFontForLanguage("bo");
		font = fontManager.getUiFont();
		assertEquals("Tibetan Machine Uni", font.getFamily());
		assertEquals(14, font.getSize());

		fontManager.setUiFontForLanguage("en");
		font = fontManager.getUiFont();
		assertEquals(12, font.getSize());
	}
}
