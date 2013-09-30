package sockets_stuff;

import java.io.InputStream;
import java.io.OutputStream;

import org.vicomtech.opener.fr.postagger.Kaf;
import org.vicomtech.opener.sockets.OpenerInvokableModule;

public class OpenerInvokableModuleImpl implements OpenerInvokableModule{

	@Override
	public void execute(InputStream is, OutputStream os, String[] args) {
		new Kaf().execute(is, os, args);
	}

	
	
}
