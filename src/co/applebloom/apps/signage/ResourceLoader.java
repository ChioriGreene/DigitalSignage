package co.applebloom.apps.signage;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public class ResourceLoader
{
	private File resourcePath = null;
	private ZipFile zipLib = null;
	private boolean isZipFile = false;
	
	public ResourceLoader(File path) throws ZipException
	{
		resourcePath = path;
		isZipFile = path.getAbsolutePath().endsWith( ".zip" );
		
		if ( isZipFile )
			zipLib = new ZipFile( resourcePath );
	}
	
	/*
	 * Provide a folder or zip file that contains the resources.
	 */
	public static ResourceLoader buildLoader( String resource )
	{
		File workingWith = new File( resource );
		
		if ( !workingWith.exists() )
			return null;
		
		try
		{
			return new ResourceLoader( workingWith );
		}
		catch ( ZipException e )
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public InputStream getInputStream( String relPath ) throws ZipException, IOException
	{
		if ( isZipFile )
		{
			FileHeader header = zipLib.getFileHeader( relPath );
			if ( header == null )
				throw new IOException( "No idea what went wrong but the Zip Library returned a null file header." );
			
			if ( header.isDirectory() )
				throw new IOException( "Can not get an InputStream on a folder." );
			
			return zipLib.getInputStream( header );
		}
		else
		{
			File file = new File( resourcePath.getAbsolutePath() + System.getProperty( "file.separator", "/" ) + relPath );
			
			if ( !file.exists() )
				throw new FileNotFoundException();
			
			if ( file.isDirectory() )
				throw new IOException( "Can not get an InputStream on a folder." );
			
			return new FileInputStream( file );
		}
	}
	
	public Image getImage( String relPath )
	{
		try
		{
			InputStream is = getInputStream( relPath );
			
			BufferedInputStream in = new BufferedInputStream( is );
			
			return ImageIO.read( in );
		}
		catch ( ZipException | IOException e )
		{
			e.printStackTrace();
			return null;
		}
	}
}
