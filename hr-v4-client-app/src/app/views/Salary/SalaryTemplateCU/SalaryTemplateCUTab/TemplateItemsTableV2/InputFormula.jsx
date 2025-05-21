import React, {useRef, useState} from "react";
import {Tooltip} from "@material-ui/core";
import {useFormikContext} from "formik";

export default function InputFormula({listData, name, valueField}) {
    const inputRef = useRef(null);
    const {setFieldValue} = useFormikContext();
    const [filteredItems, setFilteredItems] = useState([]);
    const [valueData, setValueData] = useState(valueField);
    const [missingElements, setMissingElements] = useState("");

    function isSubset(parentArray, childArray) {
        const parentSet = new Set(parentArray);
        return childArray.filter((item) => isNaN(item) && !parentSet.has(item));
    }

    const handleInputChange = (e) => {
        const value = e.target.value;
        const cursorPos = e.target.selectionStart;
        const validChars = /^[\d()+\-*/.\s\w]*$/;
        if (validChars.test(value)) {
            setValueData(value);
            setFieldValue(name, value);
            // Tìm từ hiện tại tại vị trí con trỏ
            const beforeCursor = value.slice(0, cursorPos);
            const words = beforeCursor.split(/\s+/);
            const currentWord = words[words.length - 1]; // Lấy từ cuối cùng trước con trỏ

            if (currentWord.length > 0) {
                const matches = listData.filter((item) =>
                    item.toLowerCase().includes(currentWord.toLowerCase())
                );
                setFilteredItems(matches);
            } else {
                setFilteredItems([]);
            }
        }
    };

    const handleSelectItem = (item) => {
        setMissingElements("");
        if (!inputRef.current) return;

        const cursorPos = inputRef.current.selectionStart;
        const beforeCursor = valueData.slice(0, cursorPos);
        const afterCursor = valueData.slice(cursorPos);

        // Tìm từ hiện tại trong phần trước con trỏ để thay thế
        const words = beforeCursor.split(/\s+/);
        words.pop();
        words.push(item);

        const newValue = words.join(" ") + afterCursor;
        setValueData(newValue);
        setFieldValue(name, newValue);
        setFilteredItems([]);

        setTimeout(() => inputRef.current.focus(), 0);
    };

    const handleCheckMissing = () => {
        const valueCurrent = valueData
            .split(/\s*[+\-*/()]\s*/)
            .map((value) => value.trim())
            .filter((value) => value.length > 0);

        const missingElements = isSubset(listData, valueCurrent);
        if (missingElements.length > 0) {
            setMissingElements(`Các giá trị không có trong bảng lương: ${missingElements.join(" ")}`);
            inputRef.current.style.borderColor = "red";
        } else {
            setMissingElements("")
            inputRef.current.style.borderColor = "";
        }
    };

    return (
        <div className="p-0 h-full w-full relative">
            <Tooltip title={missingElements}>
                <input
                    ref={inputRef}
                    type="text"
                    name={name}
                    value={valueData}
                    onChange={handleInputChange}
                    onBlur={handleCheckMissing}
                    style={{
                        width: "100%",
                        border: "1px solid",
                        padding: "5px",
                        borderRadius: "4px",
                    }}
                />
            </Tooltip>
            {filteredItems.length > 0 && (
                <ul
                    className="absolute bg-white border rounded shadow-md w-full mt-1 max-h-40 overflow-auto"
                    style={{zIndex: 10}}
                >
                    {filteredItems.map((item, index) => (
                        <li
                            key={index}
                            className="p-2 cursor-pointer hover:bg-gray-200"
                            onClick={() => handleSelectItem(item)}
                        >
                            {item}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}
