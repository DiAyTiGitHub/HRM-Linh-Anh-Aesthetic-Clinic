import React, { memo, useEffect, useMemo, useRef, useState } from "react";
import { FastField, getIn } from "formik";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
import { uploadImage } from "app/views/profile/ProfileService";

function GlobitsEditor(props) {
    const { label, oldStyle } = props;

    return (
        <FastField {...props} name={props?.name} shouldUpdate={shouldComponentUpdate}>
            {({ field, form }) => (
                <>
                    {label && (
                        <label htmlFor={props?.name} className={`${oldStyle ? "old-label" : "label-container"}`}>
                            {label} {props?.validate ? <span style={{ color: "red" }}> * </span> : <></>}
                        </label>
                    )}
                    <MyComponent {...props} field={field} setFieldValue={form.setFieldValue} />
                </>
            )}
        </FastField>
    );
}

function MyComponent({ disabled, field, name, setFieldValue, placeholder, readOnly, oldStyle = false }) {
    const quillRef = useRef();
    const [data, setData] = useState(field.value);

    function handleChange(value) {
        setData(value);
        setFieldValue(name, value);
    }

    useEffect(() => {
        setData(field?.value ?? null);
    }, [field?.value]);

    const imageHandler = (e) => {
        const editor = quillRef.current.getEditor();
        // console.log(editor);
        const input = document.createElement("input");
        input.setAttribute("type", "file");
        input.setAttribute("accept", "image/*");
        input.click();

        input.onchange = async () => {
            const file = input.files[0];
            if (/^image\//.test(file.type)) {
                console.log(file);
                const formData = new FormData();
                formData.append("image", file);
                const res = await uploadImage(formData); // upload data into server or aws or cloudinary
                const url = res?.data?.url;
                editor.insertEmbed(editor.getSelection(), "image", url);
            } else {
                // ErrorToast("You could only upload images.");
                console.log("You could only upload images.");
            }
        };
    };

    const modules = useMemo(() => {
        if(readOnly) {
            return {
                toolbar: false,
            };
        }

        return {
            clipboard: {
                matchVisual: false,
            },
            toolbar: {
                container: [
                    [{ header: [1, 2, 3, 4, 5, 6, false] }],
                    ["bold", "italic", "underline", "strike"],
                    [{ list: "ordered" }, { list: "bullet" }, { indent: "-1" }, { indent: "+1" }],
                    ["image", "link"],
                    [
                        {
                            color: [
                                "#000000",
                                "#e60000",
                                "#ff9900",
                                "#ffff00",
                                "#008a00",
                                "#0066cc",
                                "#9933ff",
                                "#ffffff",
                                "#facccc",
                                "#ffebcc",
                                "#ffffcc",
                                "#cce8cc",
                                "#cce0f5",
                                "#ebd6ff",
                                "#bbbbbb",
                                "#f06666",
                                "#ffc266",
                                "#ffff66",
                                "#66b966",
                                "#66a3e0",
                                "#c285ff",
                                "#888888",
                                "#a10000",
                                "#b26b00",
                                "#b2b200",
                                "#006100",
                                "#0047b2",
                                "#6b24b2",
                                "#444444",
                                "#5c0000",
                                "#663d00",
                                "#666600",
                                "#003700",
                                "#002966",
                                "#3d1466",
                            ],
                        },
                    ],
                ],
                handlers: {
                    image: imageHandler,
                },
            },
        };
    }, []);

    return (
        <ReactQuill
            ref={quillRef}
            theme='snow'
            value={data}
            onChange={handleChange}
            placeholder={placeholder}
            readOnly={readOnly}
            modules={modules}
            className={`bg-white ${oldStyle ? "" : "editor-container"} ${readOnly ? "read-only" : ""}`} // Thêm class read-only
        />
    );
}

export default memo(GlobitsEditor);

const shouldComponentUpdate = (nextProps, currentProps) => {
    return (
        nextProps.name !== currentProps.name ||
        nextProps.value !== currentProps.value ||
        nextProps.onChange !== currentProps.onChange ||
        nextProps.label !== currentProps.label ||
        nextProps.oldStyle !== currentProps.oldStyle ||
        nextProps.required !== currentProps.required ||
        nextProps.disabled !== currentProps.disabled ||
        nextProps.readOnly !== currentProps.readOnly ||
        nextProps.className !== currentProps.className ||
        nextProps.formik.isSubmitting !== currentProps.formik.isSubmitting ||
        Object.keys(nextProps).length !== Object.keys(currentProps).length ||
        getIn(nextProps.formik.values, currentProps.name) !== getIn(currentProps.formik.values, currentProps.name) ||
        getIn(nextProps.formik.errors, currentProps.name) !== getIn(currentProps.formik.errors, currentProps.name) ||
        getIn(nextProps.formik.touched, currentProps.name) !== getIn(currentProps.formik.touched, currentProps.name)
    );
};
