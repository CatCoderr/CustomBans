package me.catcoder.custombans.database;

/**
 * Created by Ruslan on 12.03.2017.
 */
public interface ResponseHandler<H, R> {


    R handleResponse(H handle) throws Exception;
}
