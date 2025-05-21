import React from 'react'
import { useEffect } from 'react';
import { useState } from 'react';
import ConstantList from '../appConfig';

export default function GlobitsAvatar({ name, imgPath, isFile, style,className }) {

    const [errorLinkImg, setErrorLinkImg] = useState(false)

    const getImageNameAndType = (name) => {
        if (name) {
            return name.split(".")[0] + "/" + name.split(".")[1];
        }
        return "";
    };

    const getLastName = (name) => {
        if (name) {
            return name.split(" ").pop().charAt(0);
        }
        return "";
    };

    useEffect(() => {
        setErrorLinkImg(false)
    }, [isFile, imgPath]);


    if (imgPath && !errorLinkImg) {
        if (isFile) {
            return <img style={{ ...style }} className={`${className} avatar-new `} src={imgPath} alt="..." onError={e => setErrorLinkImg(true)} />
        }
        const linkImg = ConstantList.API_ENPOINT + "/public/hr/file/getImage/" + getImageNameAndType(imgPath);
        return (
            <img style={{ ...style}} className={`${className} avatar-new `} alt="avatar" src={linkImg} onError={e => setErrorLinkImg(true)} />
        )
    } else {
        return (
            <div className={` ${className} avatar-new `} style={{ backgroundColor: '#5899ca7d', ...style }}>{name ? getLastName(name) : 'NS'}</div>
        )
    }
}