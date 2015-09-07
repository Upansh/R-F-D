package mypack.service;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class Update
{
	private HttpServletRequest request;
    private static HttpServletResponse response;
    public Update with(HttpServletRequest httpRequest)
	{
    	request = httpRequest;
        return this;
    }
    
    public Update with(HttpServletResponse httpResponse)
	{
    	response = httpResponse;
        return this;
    }
    private void fileUrl(String fAddress, String filename) 
	{
		DataOutputStream outStream = null;
		URLConnection  uCon = null;
		DataInputStream is = null;
		try
	    {
			int ByteRead,ByteWritten=0,data=0;
			outStream = new DataOutputStream(new BufferedOutputStream(response.getOutputStream()));
			uCon = new URL(fAddress).openConnection();
			is = new DataInputStream(new BufferedInputStream(uCon.getInputStream()));
			SimpleDateFormat d = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
			int stored = Integer.valueOf(request.getHeader("Downloaded"));
			if(stored == 0)
			{
				new Download().with(request).with(response).fileDownload(fAddress);
			}
			else
			{
				if(d.parse(d.format(Long.parseLong(request.getHeader("Last-Modified")))).after(d.parse(uCon.getHeaderField("Last-Modified"))))  // to check if requested file was modified later
				{
					response.setHeader("Content-Disposition","inline; filename=" + filename);
					response.setHeader("Last-Modified", uCon.getHeaderField("Last-Modified"));
					response.setContentType(uCon.getContentType());
					int range = ((request.getHeader("Buffer_Range"))!=null)?(Integer.valueOf(request.getHeader("Buffer_Range"))):(1024);
					byte[] buf = new byte[1];
					System.out.println("process started");
					while((data<stored)&&(ByteRead = is.read(buf)) != -1)
						data += ByteRead;
					buf = new byte[range];
					System.out.println("updating...");
					while ((ByteRead = is.read(buf)) != -1)
					{
						outStream.write(buf, 0, ByteRead);
						ByteWritten += ByteRead;
					}
					System.out.println("\nFile name : \""+filename+ "\" size : "+stored+"\nNo of bytes already stored : " + data + "\nNo of bytes successfully updated : " + ByteWritten);
				}
				else
				{
					System.out.println("The file at server has been modified...");
					response.sendError(HttpServletResponse.SC_RESET_CONTENT);
				}
			}
	    }
		catch (Exception e)
		{
			System.out.println("Error occured in connection : " +e);	
		}
		finally
		{
			try
			{
				is.close();
				outStream.flush();
				outStream.close();
			}
			catch(IOException e)
			{
				System.out.println("Error occured in closing connection : " +e);
			}
		}
	}
	public void fileDownload(String fAddress)
	{    
		int slashIndex =fAddress.lastIndexOf('/');
		int periodIndex =fAddress.lastIndexOf('.');
		String fileName=fAddress.substring(slashIndex + 1);
		if (periodIndex >=1 &&  slashIndex >= 0 && slashIndex < fAddress.length()-1)
			fileUrl(fAddress,fileName);
		else
			System.err.println("path or file name.");
	}
}
