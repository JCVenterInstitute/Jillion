package org.jcvi.glyph.phredQuality;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.Test;

public class TestZipPhredQualityCodec {

	ZipPhredQualityCodec sut = new ZipPhredQualityCodec();
	
	@Test
	public void empty(){		
		List<PhredQuality> list = Collections.<PhredQuality>emptyList();
		byte[] encoded =sut.encode(list);
		assertThat(sut.decode(encoded), is(list));
	}
	@Test
	public void oneValue(){		
		List<PhredQuality> list = PhredQuality.valueOf(
				new byte[]{20});
		byte[] encoded =sut.encode(list);
		assertThat(sut.decode(encoded), is(list));
	}
	@Test
	public void twoValues(){		
		List<PhredQuality> list = PhredQuality.valueOf(
				new byte[]{20,30});
		byte[] encoded =sut.encode(list);
		assertThat(sut.decode(encoded), is(list));
	}
	@Test
	public void manyValues(){		
		List<PhredQuality> list = PhredQuality.valueOf(
				new byte[]{20,30,32,33,33,35,40,40,40,45,46,60,12,8,7,5,55,3});
		byte[] encoded =sut.encode(list);
		assertThat(sut.decode(encoded), is(list));
	}
}
