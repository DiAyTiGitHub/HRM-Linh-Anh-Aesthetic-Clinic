import { FastField, getIn } from "formik";
import { isEqual } from "lodash";
import React from "react";
import MyTreeAutocomplete from "./MyTreeAutocomplete";

const TreeAutocompletePositionSelector = (props) => {
    return (
        <FastField {...props} name={props.name} shouldUpdate={shouldComponentUpdate}>
            {({ field, meta, form }) => (
                <MyTreeAutocomplete
                    {...props}
                    field={field}
                    meta={meta}
                    setFieldValue={form.setFieldValue}
                    formik={form}
                />
            )}
        </FastField>
    );
};

const shouldComponentUpdate = (nextProps, currentProps) => {
    return (
        nextProps.name !== currentProps.name ||
        nextProps.value !== currentProps.value ||
        nextProps.onChange !== currentProps.onChange ||
        nextProps.label !== currentProps.label ||
        nextProps.required !== currentProps.required ||
        nextProps.disabled !== currentProps.disabled ||
        nextProps.readOnly !== currentProps.readOnly ||
        nextProps.api !== currentProps.api ||
        !isEqual(nextProps.searchObject, currentProps.searchObject) ||
        nextProps.formik.isSubmitting !== currentProps.formik.isSubmitting ||
        getIn(nextProps.formik.values, currentProps.name) !== getIn(currentProps.formik.values, currentProps.name) ||
        getIn(nextProps.formik.errors, currentProps.name) !== getIn(currentProps.formik.errors, currentProps.name) ||
        getIn(nextProps.formik.touched, currentProps.name) !== getIn(currentProps.formik.touched, currentProps.name)
    );
};

export default React.memo(TreeAutocompletePositionSelector);
