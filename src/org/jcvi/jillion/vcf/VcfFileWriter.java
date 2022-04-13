package org.jcvi.jillion.vcf;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;


public class VcfFileWriter implements Closeable{
	
	private final VcfHeader header;

	private final PrintWriter out;
	
	private String newLine = "\n";
	
	public VcfFileWriter(VcfHeader header, PrintWriter out) {
		this.header = Objects.requireNonNull(header);
		this.out = Objects.requireNonNull(out);
		
		//write header out right away?
		StringBuilder builder = new StringBuilder(2000);
		builder.append("##fileformat=VCFv").append(header.getVersion()==null?"4.0": header.getVersion()).append(newLine);
		for(Entry<String, String> prop : header.getProperties().entrySet()) {
			if("fileformat".equals(prop.getKey())) {
				//don't write out fileformat 2x
				continue;
			}
			builder.append("##").append(prop.getKey()).append('=').append(prop.getValue()).append(newLine);
		}
		
		for(VcfContigInfo contig: header.getContigInfos()) {
			builder.append("##contig=<ID=").append(contig.getId())
					.append(",length=").append(contig.getLength())
					.append(contig.getParameters().entrySet().stream()
									.filter(e-> !("length".equals(e.getKey())) && !("ID".equals(e.getKey())))
									.map( e-> e.getKey() +"="+e.getValue())
									.collect(Collectors.joining(",", ",", ">"+newLine)));
		}
		for(VcfInfo info : header.getInfos()) {
			builder.append("##INFO=<ID=").append(info.getId())
								.append(",Number=").append(info.getNumber().toEncodedString())
								.append(",Type=").append(info.getType())
								.append(",Description=\"").append(info.getDescription()).append("\">")
								.append(newLine);
		}
		
		for(VcfFilter filter : header.getFilters()) {
			builder.append("##FILTER=<ID=").append(filter.getId())
			.append(",Description=\"").append(filter.getDescription()).append("\">")
			.append(newLine);
		}
		
		for(VcfFormat format : header.getFormats()) {
			builder.append("##FORMAT=<ID=").append(format.getId())
			.append(",Number=").append(format.getNumber().toEncodedString())
			.append(",Type=").append(format.getType())
			.append(",Description=\"").append(format.getDescription()).append("\">")
			.append(newLine);
		}
		//chrom line with extra headers
		String extraCols= header.getExtraColumns().stream().collect(Collectors.joining("\t", "\t", ""));
		
		builder.append("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT");
		if(!extraCols.isEmpty()) {
			builder.append(extraCols);
		}
		builder.append(newLine);
		
		
		out.write(builder.toString());
	}
	
	private String handleBlank(String f) {
		if(f==null || f.isBlank()) {
			return ".";
		}
		return f;
	}
	public void writeData(String chromId, int position, String id, String refBase, String altBase,
			int quality, String filter, String info, String format, List<String> extraFields) throws IOException {
		//TODO should we do any validation on info and format?
		
		StringBuilder builder = new StringBuilder(2000);
		builder.append(handleBlank(chromId)).append('\t')
				.append(position).append('\t')
				.append(handleBlank(id)).append('\t')
				.append(refBase).append('\t')
				.append(handleBlank(altBase)).append('\t')
				.append(quality).append('\t')
				.append(handleBlank(filter)).append('\t')
				.append(handleBlank(info)).append('\t')
				.append(handleBlank(format));
		for(String extra : extraFields) {
			builder.append('\t').append(handleBlank(extra));
		}
		builder.append(newLine);
		out.write(builder.toString()); 
	}

	@Override
	public void close() throws IOException {
		out.close();
		
	}
	
	

}
