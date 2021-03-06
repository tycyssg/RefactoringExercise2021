/*
 *
 * This class is for accessing, creating and modifying records in a file
 *
 * */

import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JOptionPane;

public class RandomFile {
	private RandomAccessFile output;
	private RandomAccessFile input;

	// Create new file
	public void createFile(String fileName) {
		RandomAccessFile file = null;

		try // open file for reading and writing
		{
			file = new RandomAccessFile(fileName, "rw");

		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error processing file!");
			System.exit(1);
		} // end catch

		finally {
			try {
				if (file != null)
					file.close(); // close file
			} // end try
			catch (IOException ioException) {
				JOptionPane.showMessageDialog(null, "Error closing file!");
				System.exit(1);
			} // end catch
		} // end finally
	} // end createFile

	// Open file for adding or changing records
	public void openWriteFile(String fileName) {
		try // open file
		{
			output = new RandomAccessFile(fileName, "rw");
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File does not exist!");
		} // end catch
	} // end method openFile

	// Close file for adding or changing records
	public void closeWriteFile() {
		try // close file and exit
		{
			if (output != null)
				output.close();
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error closing file!");
			System.exit(1);
		} // end catch
	} // end closeFile

	// Add records to file
	public long addRecords(Employee employeeToAdd) {
		try // output values to file
		{
			// object to be written to file
			RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord(employeeToAdd.getEmployeeId(), employeeToAdd.getPps(),
					employeeToAdd.getSurname(), employeeToAdd.getFirstName(), employeeToAdd.getGender(),
					employeeToAdd.getDepartment(), employeeToAdd.getSalary(), employeeToAdd.getFullTime());

			output.seek(output.length());// Look for proper position
			record.write(output);// Write object to file
			long currentRecordStart = output.length();
			// Return position where object starts in the file
			return currentRecordStart - RandomAccessEmployeeRecord.SIZE;
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
			return 0;
		} // end catch
	}// end addRecords

	// Change details for existing object
	public void changeRecords(Employee newDetails, long byteToStart) {
		// object to be written to file
		RandomAccessEmployeeRecord record;
		try // output values to file
		{
			record = new RandomAccessEmployeeRecord(newDetails.getEmployeeId(), newDetails.getPps(),
					newDetails.getSurname(), newDetails.getFirstName(), newDetails.getGender(),
					newDetails.getDepartment(), newDetails.getSalary(), newDetails.getFullTime());

			output.seek(byteToStart);// Look for proper position
			record.write(output);// Write object to file
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		} // end catch
	}// end changeRecords

	// Delete existing object
	public void deleteRecords(long byteToStart) {
		// object to be written to file
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();// Create empty object;

		try // output values to file
		{
			output.seek(byteToStart);// Look for proper position
			record.write(output);// Replace existing object with empty object
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		} // end catch
	}// end deleteRecords

	// Open file for reading
	public void openReadFile(String fileName) {
		try // open file
		{
			input = new RandomAccessFile(fileName, "r");
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File is not supported!");
		} // end catch
	} // end method openFile

	// Close file
	public void closeReadFile() {
		try // close file and exit
		{
			if (input != null)
				input.close();
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error closing file!");
			System.exit(1);
		} // end catch
	} // end method closeFile

	// Get position of first record in file
	public long getFirst() {
		return 0;
	}// end getFirst

	// Get position of last record in file
	public long getLast() {
		try {// try to get position of last record
			return (input.length() - RandomAccessEmployeeRecord.SIZE);
		}// end try
		catch (IOException ignored) {
			return 0;
		}// end catch

	}// end getFirst

	// Get position of next record in file
	public long getNext(long readFrom) {
		try {// try to read from file
			input.seek(readFrom);// Look for proper position in file
			// if next position is end of file go to start of file, else get next position
			if (readFrom + RandomAccessEmployeeRecord.SIZE == input.length())
				readFrom = 0;
			else
				readFrom = readFrom + RandomAccessEmployeeRecord.SIZE;
		} // end try
		catch (NumberFormatException | IOException ignored) {
		} // end catch

		return readFrom;
	}// end getFirst

	// Get position of previous record in file
	public long getPrevious(long readFrom) {
		try {// try to read from file
			input.seek(readFrom);// Look for proper position in file
			// if previous position is start of file go to end of file, else get previous position
			if (readFrom == 0)
				return input.length() - RandomAccessEmployeeRecord.SIZE;

			return (readFrom - RandomAccessEmployeeRecord.SIZE);
		} // end try
		catch (NumberFormatException | IOException ignored) {
			return 0;
		} // end catch

	}// end getPrevious

	// Get object from file in specified position
	public Employee readRecords(long byteToStart) {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {// try to read file and get record
			input.seek(byteToStart);// Look for proper position in file
			record.read(input);// Read record from file
		} // end try
		catch (IOException ignored) {
		}// end catch

		return record;
	}// end readRecords

	// Check if PPS Number already in use
	public boolean isPpsExist(String pps, long currentByteStart) {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
		boolean ppsExist = false;
		long currentByte = 0;

		try {// try to read from file and look for PPS Number
			// Start from start of file and loop until PPS Number is found or search returned to start position
			while (currentByte != input.length() && !ppsExist) {
				//if PPS Number is in position of current object - skip comparison
				if (currentByte != currentByteStart) {
					input.seek(currentByte);// Look for proper position in file
					record.read(input);// Get record from file
					// If PPS Number already exist in other record display message and stop search
					if (record.getPps().trim().equalsIgnoreCase(pps)) {
						ppsExist = true;
						JOptionPane.showMessageDialog(null, "PPS number already exist!");
					}// end if
				}// end if
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}// end while
		} // end try
		catch (IOException ignored) {
		}// end catch

		return ppsExist;
	}// end isPpsExist

	// Check if any record contains valid ID - greater than 0
	public boolean isSomeoneToDisplay() {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
		boolean someoneToDisplay = false;
		long currentByte = 0;

		try {// try to read from file and look for ID
			// Start from start of file and loop until valid ID is found or search returned to start position
			while (currentByte != input.length() && !someoneToDisplay) {
				input.seek(currentByte);// Look for proper position in file
				record.read(input);// Get record from file
				// If valid ID exist in stop search
				if (record.getEmployeeId() > 0)
					someoneToDisplay = true;
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}// end while
		}// end try
		catch (IOException ignored) {
		}// end catch

		return someoneToDisplay;
	}// end isSomeoneToDisplay

}// end class RandomFile
