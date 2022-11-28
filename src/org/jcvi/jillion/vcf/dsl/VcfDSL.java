package org.jcvi.jillion.vcf.dsl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.io.OutputStreams;
import org.jcvi.jillion.vcf.VcfFileWriter;
import org.jcvi.jillion.vcf.VcfFilter;
import org.jcvi.jillion.vcf.VcfFormat;
import org.jcvi.jillion.vcf.VcfHeader;
import org.jcvi.jillion.vcf.VcfHeader.VcfHeaderBuilder;
import org.jcvi.jillion.vcf.VcfInfo;
import org.jcvi.jillion.vcf.VcfInfo.VcfInfoBuilder;
import org.jcvi.jillion.vcf.dsl.VcfDSL.VcfFile.GenotypeData.GenotypeDataBuilder;
import org.jcvi.jillion.vcf.VcfNumber;
import org.jcvi.jillion.vcf.VcfValueType;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
/**
 * Domain Specific Language for creating VCF files.
 * @author dkatzel
 * 
 * @since 6.0
 *
 */
public class VcfDSL {

	private VcfHeaderBuilder headerBuilder = VcfHeader.builder();
	
	private Map<String, VcfInfo> infoMap = new LinkedHashMap<>();
	private Map<String, VcfFilter> filters = new LinkedHashMap<>();
	private Map<String, VcfFormat> formats = new LinkedHashMap<>();
	
	private Set<String> knownGenotypes = new HashSet<>();
	
	public class LineModeVcfDSL{
		private VcfHeader header;
		private Set<String> knownGenotypeNames;
		private boolean addedGenotypes=false;
		private Map<String, Map<Integer, LineDSL>> linesByCoord = new TreeMap<>();
		public LineModeVcfDSL(VcfHeader header) {
			this.header = header;
			knownGenotypeNames = new LinkedHashSet<>(header.getExtraColumns());
		}

		public synchronized LineDSL line(String chromId, int position, String id, String refBase, String altBase, int quality) {
			
			
			return linesByCoord.computeIfAbsent(chromId, k-> new TreeMap<>())
						.computeIfAbsent(position, p-> new LineDSL(this, header, chromId, position, id, refBase, altBase, quality));
			
		}
		
		
		
		private synchronized void addGenotypeNameIfNeeded(String name) {
			//just add them to our internal set
			//we will add the new names to the header in #done() so we only modify header once.
			if(knownGenotypeNames.add(name)) {
				addedGenotypes=true;
			}
		}
		
		
		public synchronized VcfFile done() {
			if(addedGenotypes) {
				header = header.toBuilder().extraColumns(knownGenotypeNames).build();
			}
			return new VcfFile(header, 
					linesByCoord.values().stream()
									.flatMap(m-> m.values().stream())
									.collect(Collectors.toList()));
		}
	}
	public enum WriteMode{
		VCF,
		BCF
	}
	public static class VcfFile{
		
		
		private final VcfHeader header;
		private List<LineDSL> lines;
		
		public VcfFile(VcfHeader header, List<LineDSL> lines) {
			this.header = header;
			this.lines = lines;
		}
		
		public void writeVcf(File vcf) throws IOException{
			IOUtil.mkdirs(vcf.getParentFile());
			try(OutputStream out = OutputStreams.buffered(vcf)){
				writeVcf(out);
			}
		}
		public void writeVcf(OutputStream vcfOutputStream) throws IOException{
			try(PrintWriter writer = new PrintWriter(vcfOutputStream, false)){
				writeVcf(writer);
			}
		}
		
		private void writeVcf(PrintWriter out) throws IOException{
			try(VcfFileWriter writer = new VcfFileWriter(header, out)){
				for(LineDSL line : lines) {
					
					//TODO refactor to support BCF
					GenotypeData genotype = vcfEncodeGenotype(line);
					writer.writeData(line.chromId, line.position, line.id, line.refBase, vcfEncodeAltBases(line.altBases), line.quality, 
							vcfEncodeFilters(line), vcfEncodeInfo(line), genotype.formatField, genotype.extraLines);
				}
			}
			
		}
		@Data
		@Builder
		public static class GenotypeData{
			private String formatField;
			@Singular
			private List<String> extraLines;
			public static final GenotypeData EMPTY = new GenotypeData(null,null);
			
			public static GenotypeDataBuilder builder() {
				return new GenotypeDataBuilder()
								.extraLines(new ArrayList<>());
			}
			//required for javadoc..
			public static class GenotypeDataBuilder{
				
			}
		}
		private String vcfEncodeAltBases(Set<String> bases) {
			return bases.stream().collect(Collectors.joining(","));
		}
		private GenotypeData vcfEncodeGenotype(LineDSL line ) {
			if(line.genotypes==null) {
				return GenotypeData.EMPTY ;
			}
			GenotypeDataBuilder builder = GenotypeData.builder();
			List<String> formatIds = new ArrayList<String>();
			
			for(AbstractVcfFormatDSL<?> format: line.genotypes.formats) {
				formatIds.add(format.format.getId());
			}
			builder.formatField(formatIds.stream().collect(Collectors.joining(":")));
			
			for(String genotype : header.getExtraColumns()) {
				GenotypeValuesDSL values = line.genotypes.values.get(genotype);
				if(values==null) {
					//empty?
					//is this possible?
					builder.extraLine(null);
				}else {
					List<String> fields = new ArrayList<>();
					int fieldsWithData = -1;
					int i=0;
					Map<AbstractVcfFormatDSL<?>, List<?>> map = values.values;
					
					for(AbstractVcfFormatDSL<?> format: line.genotypes.formats) {
						List<?> v = map.get(format);
						if(v==null || v.isEmpty()) {
							fields.add(".");
						}else {
							fieldsWithData=i;
							fields.add(v.stream().map(f-> f==null?"." : f)
									.map(Objects::toString)
									
									
									.collect(Collectors.joining(",")));
						}
						i++;
						
					}
					if(fieldsWithData>=0) {
						builder.extraLine(fields.subList(0, fieldsWithData +1).stream().collect(Collectors.joining(":")));
					}
				}
			}
			
			
			return builder.build();
		}
		
		
		private String vcfEncodeInfo(LineDSL line) {
			/*
			 * additional information: Semicolon-separated series of additional information fields, or the MISSING
value ‘.’ if none are present. Each subfield consists of a short key with optional values in the format:
key[=value[, . . . ,value]]
			 */
			if(line.infos.isEmpty()) {
				return ".";
			}
			return line.infos.stream()
					.map(info -> {
						if(info.hasValue()) {
							return info.getId() + "=" + info.getValues().stream().map(Objects::toString).collect(Collectors.joining(","));
						}else {
							return info.getId();
						}
					})
					.collect(Collectors.joining(";"));
		}
		private String vcfEncodeFilters(LineDSL line) {
			/*
			 * filter status: PASS if this position has passed all filters, i.e., a call is made at this position.
Otherwise, if the site has not passed all filters, a semicolon-separated list of codes for filters that fail. e.g.
“q10;s50” might indicate that at this site the quality is below 10 and the number of samples with data is below
50% of the total number of samples. ‘0’ is reserved and must not be used as a filter String. If filters have
not been applied, then this field must be set to the MISSING value.
			 */
			if(line.passedFiltering) {
				return "PASS";
			}
			if(line.filters.isEmpty()) {
				return ".";
			}
			return line.filters.stream()
						.map(VcfFilter::getId)
						.collect(Collectors.joining(";"));
		}
	}
	public LineModeVcfDSL beginLines() {

		headerBuilder.filters(filters.values());
		headerBuilder.infos(infoMap.values());
		headerBuilder.formats(formats.values());
		
		return new LineModeVcfDSL(headerBuilder.build());
	}
	public VcfFilter filter(String id, String description) {
		VcfFilter filter = new VcfFilter(id, description);
//		headerBuilder.filter(filter);
		filters.put(id, filter);
		return filter;
	}
	
	public VcfDSL addGenotype(String genotype) {
		if(knownGenotypes.add(genotype)) {
			headerBuilder.extraColumn(genotype);
		}
		return this;
	}
	
	public VcfDSL property(String key, String value) {
		headerBuilder.property(key, value);
		return this;
	}
	
	private void _addInfo(VcfInfo info) {
		infoMap.put(info.getId(), info);
	}
	private void _addFormat(VcfFormat format) {
		formats.put(format.getId(), format);
//		headerBuilder.format(format);
	}
	
	public IntVcfFormatDSL intFormat(VcfFormat format) {
		if(format.getType() != VcfValueType.Integer) {
			throw new IllegalArgumentException("format must have integer vaue type : " + format);
		}
		_addFormat(format);
		return new IntVcfFormatDSL(format);
	}
	public FloatVcfFormatDSL floatFormat(VcfFormat format) {
		if(format.getType() != VcfValueType.Float) {
			throw new IllegalArgumentException("format must have float vaue type : " + format);
		}
		_addFormat(format);
		return new FloatVcfFormatDSL(format);
	}
	public CharacterVcfFormatDSL charFormat(VcfFormat format) {
		if(format.getType() != VcfValueType.Character) {
			throw new IllegalArgumentException("format must have Character vaue type : " + format);
		}
		_addFormat(format);
		return new CharacterVcfFormatDSL(format);
	}
	public StringVcfFormatDSL stringFormat(VcfFormat format) {
		if(format.getType() != VcfValueType.String) {
			throw new IllegalArgumentException("format must have String vaue type : " + format);
		}
		_addFormat(format);
		return new StringVcfFormatDSL(format);
	}
	
	public IntVcfInfoDSL intInfo(VcfInfo info) {
		if(info.getType() != VcfValueType.Integer) {
			throw new IllegalArgumentException("info must have integer vaue type : " + info);
		}
		_addInfo(info);
		return new IntVcfInfoDSL(info);
	}
	public FloatVcfInfoDSL floatInfo(VcfInfo info) {
		if(info.getType() != VcfValueType.Float) {
			throw new IllegalArgumentException("info must have float vaue type : " + info);
		}
		_addInfo(info);
		return new FloatVcfInfoDSL(info);
	}
	public StringVcfInfoDSL stringInfo(VcfInfo info) {
		if(info.getType() != VcfValueType.String) {
			throw new IllegalArgumentException("info must have String vaue type : " + info);
		}
		_addInfo(info);
		return new StringVcfInfoDSL(info);
	}
	public FlagVcfInfoDSL flagInfo(VcfInfo info) {
		if(info.getType() != VcfValueType.Flag) {
			throw new IllegalArgumentException("info must have String vaue type : " + info);
		}
		_addInfo(info);
		return new FlagVcfInfoDSL(info);
	}
	public CharacterVcfInfoDSL charInfo(VcfInfo info) {
		if(info.getType() != VcfValueType.Character) {
			throw new IllegalArgumentException("info must have Character vaue type : " + info);
		}
		_addInfo(info);
		return new CharacterVcfInfoDSL(info);
	}
	
	public IntVcfInfoCreator intInfoPerAlt(String id) {
		IntVcfInfoCreator infoCreator = new IntVcfInfoCreator(VcfInfo.builder()
				.id(id)
				.number(VcfNumber.A)
				.type(VcfValueType.Integer),
				this);
		
		
		return infoCreator;
				
		
	}
	public FloatVcfInfoCreator floatInfoPerAlt(String id) {
		FloatVcfInfoCreator infoCreator = new FloatVcfInfoCreator(VcfInfo.builder()
				.id(id)
				.number(VcfNumber.A)
				.type(VcfValueType.Float),
				this);
		
		return infoCreator;
	}
	
	public CharVcfInfoCreator charInfoPerAlt(String id) {
		CharVcfInfoCreator infoCreator = new CharVcfInfoCreator(this,
				VcfInfo.builder()
						.id(id)
						.number(VcfNumber.A)
						.type(VcfValueType.Character));
		
		return infoCreator;
	}
	public FlagVcfInfoCreator flagInfoPerAlt(String id) {
		FlagVcfInfoCreator infoCreator = new FlagVcfInfoCreator(VcfInfo.builder()
				.id(id)
				.number(VcfNumber.A)
				.type(VcfValueType.Flag),
				this);
		
		return infoCreator;
	}
	
	public StringVcfInfoCreator stringInfoPerAlt(String id) {
		StringVcfInfoCreator infoCreator = new StringVcfInfoCreator(VcfInfo.builder()
				.id(id)
				.number(VcfNumber.A)
				.type(VcfValueType.Flag),
				this);
		
		return infoCreator;
	}
	public static class GenotypeDSL{
		private final LineDSL parent;
		
		

		private List<AbstractVcfFormatDSL<?>> formats = new ArrayList<>();
		private Map<String, GenotypeValuesDSL> values = new HashMap<>();
		
		private GenotypeDSL(LineDSL parent, List<AbstractVcfFormatDSL<?>> formats) {
			this.parent = parent;
			this.formats = formats;
		}
		
		public GenotypeValuesDSL add(String name) {
			parent.parent.addGenotypeNameIfNeeded(name);
			GenotypeValuesDSL dsl = new GenotypeValuesDSL(this);
			values.put(name, dsl);
			return dsl;
		}
		
		public LineDSL done() {
			return parent;
		}
		public LineModeVcfDSL doneAllGenotypes() {
			return parent.parent;
		}
	}
	
	public static class GenotypeValuesDSL{
		private final GenotypeDSL parent;
		private final Map<AbstractVcfFormatDSL<?>, List<?>> values = new HashMap<>();
		public GenotypeValuesDSL(GenotypeDSL parent) {
			super();
			this.parent = parent;
		}
		
		public GenotypeValuesDSL set(StringVcfFormatDSL type, String value) {
			values.put(type, List.of(value));
			return this;
		}
		public GenotypeValuesDSL set(StringVcfFormatDSL type, String... values) {
			
			this.values.put(type, List.of(values));
			return this;
		}
		
		public GenotypeValuesDSL set(FloatVcfFormatDSL type, float value) {
			this.values.put(type, List.of(value));
			return this;
		}
		public GenotypeValuesDSL set(FloatVcfFormatDSL type, Float... values) {
			this.values.put(type, List.of(values));
			return this;
		}
		public GenotypeValuesDSL set(FloatVcfFormatDSL type, float... values) {
			this.values.put(type, List.of(values));
			return this;
		}
		public GenotypeValuesDSL set(CharacterVcfFormatDSL type, char value) {
			this.values.put(type, List.of(value));
			return this;
		}
		public GenotypeValuesDSL set(CharacterVcfFormatDSL type, Character... values) {
			this.values.put(type, List.of(values));
			return this;
		}
		public GenotypeValuesDSL set(CharacterVcfFormatDSL type, char... values) {
			this.values.put(type, List.of(values));
			return this;
		}
		public GenotypeValuesDSL set(IntVcfFormatDSL type, int value) {
			this.values.put(type, List.of(value));
			return this;
		}
		public GenotypeValuesDSL set(IntVcfFormatDSL type, Integer... values) {
			this.values.put(type, Arrays.asList(values));
			return this;
		}
//		public GenotypeValuesDSL set(IntVcfFormatDSL type, int... values) {
//			this.values.put(type, List.of(values));
//			return this;
//		}
		
		public GenotypeDSL done() {
			return parent;
		}
	}
	private static Float[] toArray(float[] values) {
		Float[] array = new Float[values.length];
		for(int i=0; i<array.length; i++) {
			array[i] = values[i];
		}
		return array;
	}
	private static Float[] toFloatArray(double[] values) {
		Float[] array = new Float[values.length];
		for(int i=0; i<array.length; i++) {
			array[i] = (float)values[i];
		}
		return array;
	}
	private static Character[] toArray(char[] values) {
		Character[] array = new Character[values.length];
		for(int i=0; i<array.length; i++) {
			array[i] = values[i];
		}
		return array;
	}
	private static Integer[] toArray(int[] values) {
		Integer[] array = new Integer[values.length];
		for(int i=0; i<array.length; i++) {
			array[i] = values[i];
		}
		return array;
	}
	public static class LineDSL{
		private final LineModeVcfDSL parent;
		private final VcfHeader header;
		private final  int position;
		private final String chromId, id, refBase;
		private final Set<String> altBases =new LinkedHashSet<String>();
		private final int quality;
		private boolean passedFiltering;
		private List<VcfFilter> filters= new ArrayList<>();
		private List<InfoRecord<?>> infos = new ArrayList<>();
		private GenotypeDSL genotypes;
		
		
		private  LineDSL(LineModeVcfDSL parent, VcfHeader header, String chromId, int position, String id, String refBase, String altBase, int quality) {
			this.parent = parent;
			this.header = header;
			this.chromId= chromId;
			this.position = position;
			this.id = id;
			this.refBase = refBase;
			this.altBases.add(altBase);
			this.quality = quality;
		}
		
		public LineModeVcfDSL done() {
			return parent;
		}
		//String chromId, int position, String id, String refBase, String altBase,
//		int quality, String filter, String info, String format, List<String> extraFields
//		public void line(String )
		public LineDSL filter(VcfFilter filter) {
			filters.add(Objects.requireNonNull(filter));
			passedFiltering=false;
			return this;
		}
		public LineDSL passed() {
			passedFiltering=true;
			return this;
		}
		
		public synchronized LineDSL addAltBase(String altBase) {
			altBases.add(altBase);
			return this;
		}
		
		public LineDSL info(StringVcfInfoDSL infoType, String value) {
			infos.add(new SingleInfoRecord<String>(infoType, value));
			return this;
		}
		public LineDSL info(StringVcfInfoDSL infoType, String... values) {
			infos.add(new InfoMultiRecord<String>(infoType, values));
			return this;
		}
		public LineDSL info(FloatVcfInfoDSL infoType, double value) {
			infos.add(new SingleInfoRecord<Float>(infoType, (float)value));
			return this;
		}
		public LineDSL info(FloatVcfInfoDSL infoType, float value) {
			infos.add(new SingleInfoRecord<Float>(infoType, value));
			return this;
		}
		public LineDSL info(FloatVcfInfoDSL infoType, Float... values) {
			infos.add(new InfoMultiRecord<Float>(infoType, values));
			return this;
		}
		public LineDSL info(FloatVcfInfoDSL infoType, float... values) {
			Float[] array = toArray(values);
			return info(infoType, array);
		}
		public LineDSL info(FloatVcfInfoDSL infoType, double... values) {
			Float[] array = toFloatArray(values);
			return info(infoType, array);
		}
		
		
		

		
		public LineDSL info(CharacterVcfInfoDSL infoType, char value) {
			infos.add(new SingleInfoRecord<Character>(infoType, value));
			return this;
		}
		public LineDSL info(CharacterVcfInfoDSL infoType, Character... values) {
			infos.add(new InfoMultiRecord<Character>(infoType, values));
			return this;
		}
		public LineDSL info(CharacterVcfInfoDSL infoType, char... values) {
			
			return info(infoType, toArray(values));
		}
		
//		public LineDSL genotype(CharacterVcfFormatDSL type, char value) {
//			genotypes.add(new SingleGenotypeRecord<Character>(type, value));
//			return this;
//		}
//		public LineDSL genotype(CharacterVcfFormatDSL type, Character... values) {
//			genotypes.add(new MultiGenotypeRecord<Character>(type, values));
//			return this;
//		}
//		public LineDSL genotype(CharacterVcfFormatDSL type, char... values) {
//			genotypes.add(new MultiGenotypeRecord<Character>(type, toArray(values)));
//			return this;
//		}
		
		public synchronized GenotypeDSL genotypes(AbstractVcfFormatDSL<?>... formats) {
//			if(genotypes !=null) {
//				throw new IllegalStateException("can only call genotypes once!");
//			}
			List<AbstractVcfFormatDSL<?>> formatList = List.of(formats);
			if(genotypes !=null) {
				if(!genotypes.formats.equals(formatList)) {
					throw new IllegalStateException("format list does not match ");
				}
				return genotypes;
			}
			GenotypeDSL genotypeDsl = new GenotypeDSL(this, formatList);
			genotypes = genotypeDsl;
			return genotypeDsl;
		}
		public LineDSL info(IntVcfInfoDSL infoType, int value) {
			infos.add(new SingleInfoRecord<Integer>(infoType, value));
			return this;
		}
		public LineDSL info(IntVcfInfoDSL infoType, Integer... values) {
			infos.add(new InfoMultiRecord<Integer>(infoType, values));
			return this;
		}
		public LineDSL info(IntVcfInfoDSL infoType, int... values) {
			return info(infoType, toArray(values));
		}
//		public LineDSL genotype(IntVcfFormatDSL type, int value) {
//			genotypes.add(new SingleGenotypeRecord<Integer>(type, value));
//			return this;
//		}
//		public LineDSL genotype(IntVcfFormatDSL type, Integer... values) {
//			genotypes.add(new MultiGenotypeRecord<Integer>(type, values));
//			return this;
//		}
//		public LineDSL genotype(IntVcfFormatDSL type, int... values) {
//			genotypes.add(new MultiGenotypeRecord<Integer>(type, toArray(values)));
//			return this;
//		}
		public LineDSL info(FlagVcfInfoDSL infoType) {
			infos.add(new FlagInfoRecord<Void>(infoType));
			return this;
		}
	}
	private interface InfoRecord<T>{
		String getId();
		boolean hasValue();
		List<T> getValues();
	}
	private interface FormatRecord<T>{
		String getId();
		List<T> getValues();
	}
	@Data
	private static class FlagInfoRecord<T> implements InfoRecord<T>{
		private final AbstractVcfInfoDSL<T> info;
		
		@Override
		public String getId() {
			return info.getInfo().getId();
		}

		@Override
		public boolean hasValue() {
			return false;
		}

		@Override
		public List<T> getValues() {
			return null;
		}
		
		
		
	}
	@Data
	private static class SingleGenotypeRecord<T> implements FormatRecord<T>{
		private final AbstractVcfFormatDSL<T> info;
		private final T value;
		@Override
		public String getId() {
			return info.getFormat().getId();
		}
		@Override
		public List<T> getValues() {
			return Collections.singletonList(value);
		}
		
	}
	@Data
	private static class MultiGenotypeRecord<T> implements FormatRecord<T>{
		private final AbstractVcfFormatDSL<T> info;
		private final T[] value;
		
		@Override
		public String getId() {
			return info.getFormat().getId();
		}
		@Override
		public List<T> getValues() {
			return List.of(value);
		}
	}
	@Data
	private static class SingleInfoRecord<T> implements InfoRecord<T>{
		private final AbstractVcfInfoDSL<T> info;
		private final T value;
		@Override
		public String getId() {
			return info.getInfo().getId();
		}
		@Override
		public List<T> getValues() {
			return Collections.singletonList(value);
		}
		@Override
		public boolean hasValue() {
			return true;
		}
		
	}
	@Data
	private static class InfoMultiRecord<T> implements InfoRecord<T>{
		private final AbstractVcfInfoDSL<T> info;
		private final T[] value;
		
		@Override
		public String getId() {
			return info.getInfo().getId();
		}
		@Override
		public List<T> getValues() {
			return List.of(value);
		}
		@Override
		public boolean hasValue() {
			return true;
		}
	}
	public static abstract class AbstractVcfInfoCreator<T extends AbstractVcfInfoCreator<?, R>, R extends AbstractVcfInfoDSL<?>>{
		protected final VcfInfoBuilder builder;

		private final VcfDSL parent;
		protected String id;
		public AbstractVcfInfoCreator(VcfInfoBuilder builder, VcfDSL parent) {
			this.builder = builder;
			this.parent = parent;
		}
		
		protected abstract T getThis();
		
		protected abstract R createInfoDSL(VcfInfo info);
		
		public R create() {
			VcfInfo info = builder.build();
			parent._addInfo(info);
			return createInfoDSL(info);
		}
		
		public T description(String description) {
			builder.description(description);
			return getThis();
		}
		
		public T parameter(String key, String value) {
			builder.parameter(key, value);
			return getThis();
		}
	}
	
	public static abstract class AbstractVcfInfoDSL<T>{
		private final VcfInfo info;

		public AbstractVcfInfoDSL(VcfInfo info) {
			this.info = info;
		}

		VcfInfo getInfo() {
			return info;
		}
		
	}
	
	public static class IntVcfInfoDSL extends AbstractVcfInfoDSL<Integer>{

		public IntVcfInfoDSL(VcfInfo info) {
			super(info);
		}
		
	}
	public static class FloatVcfInfoDSL extends AbstractVcfInfoDSL<Float>{

		public FloatVcfInfoDSL(VcfInfo info) {
			super(info);
		}
		
	}
	public static class FlagVcfInfoDSL extends AbstractVcfInfoDSL<Void>{

		public FlagVcfInfoDSL(VcfInfo info) {
			super(info);
		}
		
	}
	public static class CharacterVcfInfoDSL extends AbstractVcfInfoDSL<Character>{

		public CharacterVcfInfoDSL(VcfInfo info) {
			super(info);
		}
		
	}
	public static class StringVcfInfoDSL extends AbstractVcfInfoDSL<String>{

		public StringVcfInfoDSL(VcfInfo info) {
			super(info);
		}
		
	}
	public static class IntVcfInfoCreator extends AbstractVcfInfoCreator<IntVcfInfoCreator, IntVcfInfoDSL>{
		

		public IntVcfInfoCreator(VcfInfoBuilder builder, VcfDSL parent) {
			super(builder, parent);
		}
		
		
		@Override
		protected IntVcfInfoCreator getThis() {
			return this;
		}


		@Override
		protected IntVcfInfoDSL createInfoDSL(VcfInfo info) {
			return new IntVcfInfoDSL(info);
		}
	}
	
	public static class CharVcfInfoCreator extends AbstractVcfInfoCreator<CharVcfInfoCreator, CharacterVcfInfoDSL>{
		

		public CharVcfInfoCreator(VcfDSL parent,VcfInfoBuilder builder) {
			super(builder, parent);
		}
		
		
		@Override
		protected CharVcfInfoCreator getThis() {
			return this;
		}
		@Override
		protected CharacterVcfInfoDSL createInfoDSL(VcfInfo info) {
			return new CharacterVcfInfoDSL(info);
		}
	}
	
	
	public static class StringVcfInfoCreator extends AbstractVcfInfoCreator<StringVcfInfoCreator, StringVcfInfoDSL>{
		

		public StringVcfInfoCreator(VcfInfoBuilder builder, VcfDSL parent) {
			super(builder, parent);
		}
		
		@Override
		protected StringVcfInfoDSL createInfoDSL(VcfInfo info) {
			return new StringVcfInfoDSL(info);
		}
		@Override
		protected StringVcfInfoCreator getThis() {
			return this;
		}
	}

	public static class FlagVcfInfoCreator extends AbstractVcfInfoCreator<FlagVcfInfoCreator, FlagVcfInfoDSL>{
		

		public FlagVcfInfoCreator(VcfInfoBuilder builder, VcfDSL parent) {
			super(builder, parent);
		}
		
		@Override
		protected FlagVcfInfoDSL createInfoDSL(VcfInfo info) {
			return new FlagVcfInfoDSL(info);
		}
		
		@Override
		protected FlagVcfInfoCreator getThis() {
			return this;
		}
	}
	public static class FloatVcfInfoCreator extends AbstractVcfInfoCreator<FloatVcfInfoCreator, FloatVcfInfoDSL>{
		

		public FloatVcfInfoCreator(VcfInfoBuilder builder, VcfDSL parent) {
			super(builder, parent);
		}
		
		@Override
		protected FloatVcfInfoDSL createInfoDSL(VcfInfo info) {
			return new FloatVcfInfoDSL(info);
		}
		
		@Override
		protected FloatVcfInfoCreator getThis() {
			return this;
		}
	}
	
	
	public static abstract class AbstractVcfFormatDSL<T>{
		private final VcfFormat format;

		public AbstractVcfFormatDSL(VcfFormat format) {
			this.format = format;
		}

		VcfFormat getFormat() {
			return format;
		}
		
	}
	
	public static class IntVcfFormatDSL extends AbstractVcfFormatDSL<Integer>{

		public IntVcfFormatDSL(VcfFormat format) {
			super(format);
		}

		
		
	}
	public static class FloatVcfFormatDSL extends AbstractVcfFormatDSL<Float>{

		public FloatVcfFormatDSL(VcfFormat format) {
			super(format);
		}
		
	}
	public static class CharacterVcfFormatDSL extends AbstractVcfFormatDSL<Character>{

		public CharacterVcfFormatDSL(VcfFormat format) {
			super(format);
		}
		
	}
	public static class StringVcfFormatDSL extends AbstractVcfFormatDSL<String>{

		public StringVcfFormatDSL(VcfFormat format) {
			super(format);
		}
		
	}
}
