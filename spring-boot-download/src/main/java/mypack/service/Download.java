package mypack.service;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class Download
{
	private HttpServletRequest request;
    private static HttpServletResponse response;
    public Download with(HttpServletRequest httpRequest)
	{
    	request = httpRequest;
        return this;
    }
    
    public Download with(HttpServletResponse httpResponse)
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
			int ByteRead,ByteWritten=0;
			outStream = new DataOutputStream(new BufferedOutputStream(response.getOutputStream()));
			uCon = new URL(fAddress).openConnection();
			is = new DataInputStream(new BufferedInputStream(uCon.getInputStream()));
			response.setHeader("Content-Disposition","inline; filename=" + filename);
			response.setHeader("Last-Modified", uCon.getHeaderField("Last-Modified"));
			response.setContentType(uCon.getContentType());
			int range = ((request.getHeader("Buffer_Range"))!=null)?(Integer.valueOf(request.getHeader("Buffer_Range"))):(1024);
			byte[] buf = new byte[range];
			System.out.println("download started");
			while ((ByteRead = is.read(buf)) != -1)
			{
				outStream.write(buf, 0, ByteRead);
				ByteWritten += ByteRead;
	        }
			if(uCon.getContentLength() == ByteWritten)
				System.out.println("File \""+filename+ "\" is successfully downloaded, size : " + ByteWritten + ", content : " + uCon.getContentType());
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
				System.out.println("Error occured in closing connection : " +e);;
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
