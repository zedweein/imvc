package boot.imvc.servlet.data.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import boot.imvc.servlet.utils.TypeUtil;

public class LongHandler extends ScalarHandler<Long>{
	@Override
	public Long handle(ResultSet rs) throws SQLException {
		Object obj = super.handle(rs);
		return TypeUtil.toLong(obj, -1L);
	}
}
