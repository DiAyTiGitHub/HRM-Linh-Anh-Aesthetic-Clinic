import React, { useState, useRef, useEffect, useCallback, useMemo, forwardRef } from 'react';
import { FastField } from 'formik';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import { uploadImage } from 'app/views/profile/ProfileService';

const EditorCore = forwardRef(({ value, onChange, readOnly, disabled, placeholder }, ref) => {
    const quillRef = useRef(null);

    // Expose editor instance to parent
    React.useImperativeHandle(ref, () => ({
        getEditor: () => quillRef.current?.getEditor()
    }));

    const imageHandler = useCallback(() => {
        const editor = quillRef.current?.getEditor();
        if (!editor) return;

        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', 'image/*');
        input.click();

        input.onchange = async () => {
            const file = input.files[0];
            if (file && /^image\//.test(file.type)) {
                try {
                    const formData = new FormData();
                    formData.append('image', file);
                    const res = await uploadImage(formData);
                    const url = res?.data?.url;
                    if (url) {
                        const range = editor.getSelection(true);
                        editor.insertEmbed(range.index, 'image', url);
                        editor.setSelection(range.index + 1);
                    }
                } catch (error) {
                    console.error('Upload failed', error);
                }
            }
        };
    }, []);

    const modules = useMemo(() => ({
        toolbar: readOnly || disabled ? false : {
            container: [
                [{ header: [1, 2, 3, false] }],
                ['bold', 'italic', 'underline', 'strike'],
                [{ list: 'ordered' }, { list: 'bullet' }],
                ['link', 'image'],
                ['clean']
            ],
            handlers: { image: imageHandler }
        },
        clipboard: { matchVisual: false }
    }), [readOnly, disabled, imageHandler]);

    return (
        <ReactQuill
            ref={quillRef}
            value={value || ''}
            onChange={onChange}
            modules={modules}
            theme="snow"
            readOnly={readOnly || disabled}
            placeholder={placeholder}
            preserveWhitespace
            style={{
                border: '1px solid #ced4da',
                borderRadius: 4,
                backgroundColor: (readOnly || disabled) ? '#e9ecef' : 'white'
            }}
        />
    );
});

const FormikQuillField = ({ field, form, label, readOnly, disabled, ...props }) => {
    const [internalValue, setInternalValue] = useState(field.value || '');
    const quillRef = useRef(null);
    const selectionRef = useRef(null);

    // Save cursor position before any potential re-render
    const saveSelection = useCallback(() => {
        const editor = quillRef.current?.getEditor();
        if (editor) {
            selectionRef.current = editor.getSelection();
        }
    }, []);

    // Restore cursor position after re-render
    const restoreSelection = useCallback(() => {
        const editor = quillRef.current?.getEditor();
        if (editor && selectionRef.current) {
            setTimeout(() => {
                editor.setSelection(selectionRef.current);
            }, 0);
        }
    }, []);

    // Update Formik with debounce
    const updateFormik = useMemo(() => {
        let timeoutId;

        return (value) => {
            saveSelection();
            setInternalValue(value);

            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => {
                form.setFieldValue(field.name, value);
                restoreSelection();
            }, 300);
        };
    }, [field.name, form.setFieldValue, saveSelection, restoreSelection]);

    // Sync with external changes
    useEffect(() => {
        if (field.value !== internalValue) {
            setInternalValue(field.value || '');
        }
    }, [field.value]);

    // Focus on mount if editable
    useEffect(() => {
        if (!readOnly && !disabled) {
            const editor = quillRef.current?.getEditor();
            editor?.focus();
        }
    }, [readOnly, disabled]);

    return (
        <div style={{ marginBottom: 16 }}>
            {label && (
                <label style={{
                    display: 'block',
                    marginBottom: 8,
                    fontWeight: 500,
                    color: disabled ? '#6c757d' : '#212529'
                }}>
                    {label}
                </label>
            )}
            <EditorCore
                ref={quillRef}
                value={internalValue}
                onChange={updateFormik}
                readOnly={readOnly}
                disabled={disabled}
                {...props}
            />
        </div>
    );
};

const GlobitsQuillEditor = ({ name, label, readOnly, disabled, ...props }) => {
    return (
        <FastField name={name}>
            {({ field, form }) => (
                <FormikQuillField
                    field={field}
                    form={form}
                    label={label}
                    readOnly={readOnly}
                    disabled={disabled}
                    {...props}
                />
            )}
        </FastField>
    );
};

export default React.memo(GlobitsQuillEditor);