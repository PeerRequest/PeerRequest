package com.peerrequest.app.services;

import com.peerrequest.app.model.Category;

/**
 * Describes the functionality of a CategoryService.
 *
 * @param <F> Category Filter
 * @param <U> Category Updater
 */
public interface CategoryService<F, U>
    extends CrudService<Category, Category.CategorySelector, F, U> {
}