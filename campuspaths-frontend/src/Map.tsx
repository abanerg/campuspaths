

import {LatLngExpression} from "leaflet";
import React, {Component} from "react";
import {MapContainer, TileLayer} from "react-leaflet";
import "leaflet/dist/leaflet.css";
import MapLine from "./MapLine";
import {UW_LATITUDE_CENTER, UW_LONGITUDE_CENTER} from "./Constants";

// This defines the location of the map. These are the coordinates of the UW Seattle campus
const position: LatLngExpression = [UW_LATITUDE_CENTER, UW_LONGITUDE_CENTER];

export interface Edge { // Edge interface has all required elements of a MapLine
    x1: number,
    y1: number,
    x2: number,
    y2: number,
    color: string,
}


interface MapProps {
    edges: Edge[], // Only prop needed is the edges to be displayed, supplied by the parent
    // Whenever parent needs it to update, will pass a different array of edges
    // and Map will update
}


class Map extends Component<MapProps, {}> {

    render() {
        const mapEdges: JSX.Element[] = []; // Array of HTML elements
        if (this.props.edges.length !== 0) { // Only if there edges to use
            for (const i in this.props.edges) { // For every edge in the list of edges
                // Add a new div with a MapLine with all the required elements from edge
                // Assume parent has supplied well-formed Edge data
                let edge: Edge = this.props.edges[i];
                // Key just needs to be unique in context of list, so we can use array index since new array is created
                // on each render
                mapEdges.push(<div><MapLine key={i.toString()} color={edge.color} x1={edge.x1}
                                                                 y1 = {edge.y1} x2 = {edge.x2} y2 = {edge.y2}/></div>);
            }
        }
        return (
            <div id="map">
                <MapContainer
                    center={position}
                    zoom={15}
                    scrollWheelZoom={false}
                >
                    <TileLayer
                        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    />
                    {    // <MapLine key={key1} color="red" x1={1000} y1={1000} x2={2000} y2={2000}/>
                        // will draw a red line from the point 1000,1000 to 2000,2000 on the
                        // map
                    }
                    <div id="mapEdges">{mapEdges}</div>
                    {
                        // Take all the elements in mapEdges array and return them as part of the Map
                        // component to be rendered
                    }
                </MapContainer>

            </div>
        );
    }
}

export default Map;
