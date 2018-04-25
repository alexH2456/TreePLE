import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {compose, withProps} from 'recompose';
import {Button, Divider, Dropdown, Form, Grid, Header, Icon, List, Message, Modal} from 'semantic-ui-react';
import {GoogleMap, InfoWindow, Marker, withScriptjs, withGoogleMap} from 'react-google-maps';
import {DrawingManager} from 'react-google-maps/lib/components/drawing/DrawingManager';
import DayPickerInput from 'react-day-picker/DayPickerInput';
import {getAllTrees, createForecast} from "./Requests";
import {getLatLng, getError, getTreeMarker, formatDate} from './Utils';
import {gmapsKey, mtlCenter, huDates, flags} from '../constants';


class CreateForecastModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
      fcDate: new Date(),
      fcTrees: [],
      mapTrees: [],
      hover: null,
      language: 'en',
      showTrees: true,
      error: ''
    };
  }

  componentWillMount() {
    getAllTrees()
      .then(({data}) => {
        this.setState({mapTrees: data});
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  onCreateForecast = () => {
    const fcParams = {
      fcUser: this.state.user,
      fcDate: formatDate(this.state.fcDate),
      fcTrees: this.state.fcTrees
    };

    createForecast(fcParams)
      .then(({data}) => {
        this.props.onForecast();
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      })
  }

  onRemoveTree = (treeIdx) => {
    let fcTrees = this.state.fcTrees.slice();
    fcTrees.splice(treeIdx, 1);
    this.setState({fcTrees: fcTrees});
  }

  onTreeClick = ({treeId}) => {
    let fcTrees = this.state.fcTrees.slice();
    if (!fcTrees.includes(treeId)) {
      fcTrees.push(treeId);
      this.setState({fcTrees: fcTrees});
    }
  }

  onTreeRightClick = (tree) => {
    const hover = tree == null ? null : tree.treeId == this.state.hover ? null : tree.treeId;
    this.setState({hover: hover});
  }

  onAreaComplete = (area) => {
    let fcTrees = this.state.fcTrees.slice();

    this.state.mapTrees.forEach(tree => {
      const latLng = new google.maps.LatLng(getLatLng(tree.location));
      if ('latLngs' in area) {
        if (google.maps.geometry.poly.containsLocation(latLng, area)) {
          fcTrees.push(tree.treeId)
        }
      } else if (area.getBounds().contains(latLng)) {
        fcTrees.push(tree.treeId)
      }
    });
    area.setMap(null);
    this.setState({fcTrees: fcTrees});
  }

  onDateChange = (day, {selected}) => this.setState({fcDate: day});
  onFlagChange = (e, {value}) => this.setState({language: value});
  onShowTrees = () => this.setState(prevState => ({showTrees: !prevState.showTrees}));

  render() {
    const {mapTrees, fcTrees} = this.state;
    const errors = getError(this.state.error);

    let treeIter = [...Array(Math.ceil(this.state.fcTrees.length/8)).keys()];

    const dayPickerProps = {
      locale: this.state.language,
      weekdaysShort: this.state.language == 'hu' ? huDates.weekShort : undefined,
      weekdaysLong: this.state.language == 'hu' ? huDates.weekLong : undefined,
      months: this.state.language == 'hu' ? huDates.months : undefined
    };

    return (
      <Modal open size='large' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='wpforms' circular/>
              <Header.Content>Create Forecast</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Grid columns='equal'>
              <Grid.Column width={4}>
                <Form>
                  <Form.Input label='Date Planted' error={errors.date}>
                    <Dropdown compact icon={null} selection options={flags} defaultValue={flags[0].key} onChange={this.onFlagChange}/>
                    <DayPickerInput placeholder='YYYY-MM-DD' value={this.state.fcDate} dayPickerProps={dayPickerProps} onDayChange={this.onDateChange}/>
                  </Form.Input>
                  <Form.Input fluid readOnly label='Total Trees' placeholder='Number of Trees' type='number' min='0' value={fcTrees.length}/>
                </Form>
              </Grid.Column>
              <Grid.Column>
                <Header as='h5' textAlign='center'>
                  <Header.Content>
                    <Icon name='tree' onClick={this.onShowTrees}/>Selected Trees
                  </Header.Content>
                </Header>
                <Divider/>
                {this.state.showTrees ? (
                  <Grid columns={8}>
                    {treeIter.map(i => (
                      <Grid.Row key={i} verticalAlign='middle'>
                        <Grid.Column textAlign='center' width={2}>
                          <Button inverted circular size='mini' color='red' content={fcTrees[8*i]} icon='delete' onClick={() => this.onRemoveTree(8*i)}/>
                        </Grid.Column>
                        <Grid.Column textAlign='center' width={2}>
                          {8*i+1 in fcTrees ? <Button inverted circular size='mini' color='red' content={fcTrees[8*i+1]} icon='delete' onClick={() => this.onRemoveTree(8*i+1)}/> : null}
                        </Grid.Column>
                        <Grid.Column textAlign='center' width={2}>
                          {8*i+2 in fcTrees ? <Button inverted circular size='mini' color='red' content={fcTrees[8*i+2]} icon='delete' onClick={() => this.onRemoveTree(8*i+2)}/> : null}
                        </Grid.Column>
                        <Grid.Column textAlign='center' width={2}>
                          {8*i+3 in fcTrees ? <Button inverted circular size='mini' color='red' content={fcTrees[8*i+3]} icon='delete' onClick={() => this.onRemoveTree(8*i+3)}/> : null}
                        </Grid.Column>
                        <Grid.Column textAlign='center' width={2}>
                          {8*i+4 in fcTrees ? <Button inverted circular size='mini' color='red' content={fcTrees[8*i+4]} icon='delete' onClick={() => this.onRemoveTree(8*i+4)}/> : null}
                        </Grid.Column>
                        <Grid.Column textAlign='center' width={2}>
                          {8*i+5 in fcTrees ? <Button inverted circular size='mini' color='red' content={fcTrees[8*i+5]} icon='delete' onClick={() => this.onRemoveTree(8*i+5)}/> : null}
                        </Grid.Column>
                        <Grid.Column textAlign='center' width={2}>
                          {8*i+6 in fcTrees ? <Button inverted circular size='mini' color='red' content={fcTrees[8*i+6]} icon='delete' onClick={() => this.onRemoveTree(8*i+6)}/> : null}
                        </Grid.Column>
                        <Grid.Column textAlign='center' width={2}>
                          {8*i+7 in fcTrees ? <Button inverted circular size='mini' color='red' content={fcTrees[8*i+7]} icon='delete' onClick={() => this.onRemoveTree(8*i+7)}/> : null}
                        </Grid.Column>
                      </Grid.Row>
                    ))}
                  </Grid>
                ) : null}
              </Grid.Column>
            </Grid>

            <Header as='h3' textAlign='center'>
              <Header.Content content='Tree Map'/>
            </Header>
            <Divider/>
            <Grid centered>
              <Grid.Row>
                {/* <Button inverted color='blue' size='small' onClick={this.onClearArea}>Clear Area</Button> */}
                {/* <Dropdown inverted color='orange' size='small' onClick={this.onToggleEdit}>Back</Dropdown> */}
              </Grid.Row>
            </Grid>

            <Divider hidden/>
            <GMap trees={mapTrees} fcTrees={fcTrees} hover={this.state.hover} onTreeClick={this.onTreeClick} onTreeRightClick={this.onTreeRightClick} onAreaComplete={this.onAreaComplete}/>
            <Divider hidden/>

            {this.state.error ? (
              <Message error>
                <Message.Header style={{textAlign: 'center'}}>{this.state.error}</Message.Header>
              </Message>
            ) : null}

            <Grid centered>
              <Grid.Row>
                <Button inverted color='green' size='small' disabled={!this.state.user} onClick={this.onCreateForecast}>Create</Button>
                <Button inverted color='orange' size='small' onClick={this.props.onForecast}>Back</Button>
                <Button inverted color='red' size='small' onClick={this.props.onClose}>Close</Button>
              </Grid.Row>
            </Grid>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

const GMap = compose(
  withProps({
    googleMapURL: `https://maps.googleapis.com/maps/api/js?key=${gmapsKey}&v=3.exp&libraries=geometry,drawing,places`,
    loadingElement: <div style={{width: '100vw', height: '60vh'}}/>,
    containerElement: <div style={{height: '60vh'}}/>,
    mapElement: <div style={{height: '60vh'}}/>,
  }),
  withScriptjs,
  withGoogleMap
)(({trees, fcTrees, hover, onTreeClick, onTreeRightClick, onAreaComplete}) => {
  const OT = google.maps.drawing.OverlayType;
  const areaOptions = {fillColor: '#8BDFB9'};

  return (
    <GoogleMap zoom={14} center={mtlCenter} options={{scrollwheel: true}}>
      <DrawingManager
        options={{
          circleOptions: areaOptions,
          polygonOptions: areaOptions,
          rectangleOptions: areaOptions,
          drawingControlOptions: {
            position: google.maps.ControlPosition.TOP_CENTER,
            drawingModes: [OT.CIRCLE, OT.POLYGON, OT.RECTANGLE]
          }
        }}
        onCircleComplete={onAreaComplete}
        onPolygonComplete={onAreaComplete}
        onRectangleComplete={onAreaComplete}
      />
      {trees.map(tree => (
        <Marker
          key={tree.treeId}
          icon={{
            url: getTreeMarker(fcTrees.includes(tree.treeId) ? 'selected' : tree.status),
            anchor: new google.maps.Point(23,45),
            scaledSize: new google.maps.Size(40, 60)
          }}
          position={getLatLng(tree.location)}
          onClick={() => onTreeClick(tree)}
          onRightClick={() => onTreeRightClick(tree)}
        >
          {(hover && hover == tree.treeId) ? (
            <InfoWindow onCloseClick={() => onTreeRightClick(null)}>
              <List horizontal>
                <List.Item icon='tree'/>
                <List.Item content={tree.treeId}/>
              </List>
            </InfoWindow>
          ) : null}
        </Marker>
      ))}
    </GoogleMap>
  );
});

CreateForecastModal.propTypes = {
  onForecast: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired
}

export default CreateForecastModal;
