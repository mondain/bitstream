package javax.media.protocol;

public abstract class PushDataSource extends DataSource {

	public PushDataSource() {
		super(); // TODO: anything to do?
	}

	public abstract PushSourceStream[] getStreams();
}
