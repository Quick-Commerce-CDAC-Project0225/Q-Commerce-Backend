package com.sunbeam.dto.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageUploadResultDTO {
	private String fileName;
	private boolean success;
	private String imageUrl; // Only if success
	private String errorMessage; // Only if failed
	private long sizeInBytes;

	public void uploadSuccess(String fileName, String imageUrl, long sizeInBytes) {
		this.success = true;
		this.fileName = fileName;
		this.imageUrl = imageUrl;
		this.sizeInBytes = sizeInBytes;
	}

	public void uploadError(String errorMessage) {
		this.success = false;
		this.errorMessage = errorMessage;
	}

}
