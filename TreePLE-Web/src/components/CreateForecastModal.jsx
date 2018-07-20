import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {compose, withProps} from 'recompose';
import {Button, Divider, Dropdown, Form, Grid, Header, Icon, List, Message, Modal} from 'semantic-ui-react';
import {GoogleMap, InfoWindow, Marker, withScriptjs, withGoogleMap} from 'react-google-maps';
import {DrawingManager} from 'react-google-maps/lib/components/drawing/DrawingManager';
import DayPickerInput from 'react-day-picker/DayPickerInput';
import {getAllTrees, getAllSpecies, getAllMunicipalities, getUserTrees, createForecast} from './Requests';
import {getLatLng, getError, getSelectable, getTreeMarker, getTreeAge, formatDate} from './Utils';
import {gmapsKey, mtlCenter, huDates, flags, landSelectable, statusSelectable} from '../constants';


class CreateForecastModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
      fcDate: new Date(),
      fcTrees: [],
      myTrees: [],
      mapTrees: [],
      allTrees: [],
      filters: {
        age: {min: '', max: ''},
        height: {min: '', max: ''},
        diameter: {min: '', max: ''},
        date: {min: '', max: ''},
        species: '',
        municipality: '',
        land: '',
        status: '',
        ownership: '',
        myTrees: false
      },
      hover: null,
      language: 'en',
      speciesSelectable: [],
      municipalitySelectable: [],
      showTrees: true,
      showFilters: true,
      error: ''
    };
  }

  componentWillMount() {
    const myTreesProm = getUserTrees(localStorage.getItem('username')).then(({data}) => data).catch(({response: {data}}) => data);
    const allTreesProm = getAllTrees().then(({data}) => data).catch(({response: {data}}) => data);
    const speciesProm = getAllSpecies().then(({data}) => data).catch(({response: {data}}) => data);
    const municipalityProm = getAllMunicipalities().then(({data}) => data).catch(({response: {data}}) => data);

    Promise.all([myTreesProm, allTreesProm])
      .then(([myTrees, allTrees]) => {
        this.setState({
          myTrees: myTrees,
          mapTrees: allTrees,
          allTrees: allTrees
        });
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });

    Promise.all([speciesProm, municipalityProm])
      .then(([species, municipalities]) => {
        this.setState({
          speciesSelectable: getSelectable(species),
          municipalitySelectable: getSelectable(municipalities)
        });
      })
      .catch(() => {
        this.setState({error: 'Unable to retrieve Species/Municipality list!'});
      });
  }

  onCreateForecast = () => {
    const fcParams = {
      fcUser: this.state.user,
      fcDate: formatDate(this.state.fcDate),
      fcTrees: this.state.fcTrees
    };

    createForecast(fcParams)
      .then(() => {
        this.props.onClose(true);
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  onRemoveTree = (treeIdx) => {
    this.setState((prevState) => {
      const fcTrees = prevState.fcTrees.slice();
      fcTrees.splice(treeIdx, 1);
      return {fcTrees: fcTrees};
    });
  }

  onTreeClick = ({treeId}) => {
    if (!this.state.fcTrees.includes(treeId)) {
      this.setState((prevState) => {
        const fcTrees = prevState.fcTrees.slice();
        fcTrees.push(treeId);
        return {fcTrees: fcTrees};
      });
    }
  }

  onTreeRightClick = (tree) => {
    this.setState((prevState) => ({hover: tree === null ? null : tree.treeId === prevState.hover ? null : tree.treeId}));
  }

  onAreaComplete = (area) => {
    let fcTrees = this.state.fcTrees.slice();

    this.state.mapTrees.forEach(({treeId, location}) => {
      const latLng = new google.maps.LatLng(getLatLng(location));

      if (fcTrees.includes(treeId)) {
        return;
      } else if ('latLngs' in area && google.maps.geometry.poly.containsLocation(latLng, area)) {
        fcTrees.push(treeId);
      } else if ('getBounds' in area && area.getBounds().contains(latLng)) {
        fcTrees.push(treeId);
      }
    });
    area.setMap(null);
    this.setState({fcTrees: fcTrees});
  }

  onMinAgeFilter = (e, {value}) => this.onFilterChange({...this.state.filters, age: {...this.state.filters.age, min: value}});
  onMaxAgeFilter = (e, {value}) => this.onFilterChange({...this.state.filters, age: {...this.state.filters.age, max: value}});
  onMinHeightFilter = (e, {value}) => this.onFilterChange({...this.state.filters, height: {...this.state.filters.height, min: value}});
  onMaxHeightFilter = (e, {value}) => this.onFilterChange({...this.state.filters, height: {...this.state.filters.height, max: value}});
  onMinDiameterFilter = (e, {value}) => this.onFilterChange({...this.state.filters, diameter: {...this.state.filters.diameter, min: value}});
  onMaxDiameterFilter = (e, {value}) => this.onFilterChange({...this.state.filters, diameter: {...this.state.filters.diameter, max: value}});
  onMinDateFilter = (day) => this.onFilterChange({...this.state.filters, date: {...this.state.filters.date, min: day}});
  onMaxDateFilter = (day) => this.onFilterChange({...this.state.filters, date: {...this.state.filters.date, max: day}});
  onSpeciesFilter = (e, {value}) => this.onFilterChange({...this.state.filters, species: value});
  onMunicipalityFilter = (e, {value}) => this.onFilterChange({...this.state.filters, municipality: value});
  onStatusFilter = (e, {value}) => this.onFilterChange({...this.state.filters, status: value});
  onLandFilter = (e, {value}) => this.onFilterChange({...this.state.filters, land: value});
  onMyTreesFilter = () => this.onFilterChange({...this.state.filters, myTrees: !this.state.filters.myTrees});

  onFilterChange = (filters) => {
    let filteredTrees = [];
    let trees = filters.myTrees ? this.state.myTrees : this.state.allTrees;

    trees.forEach((tree) => {
      if ((!filters.age.min || getTreeAge(tree) > filters.age.min) &&
          (!filters.age.max || getTreeAge(tree) < filters.age.max) &&
          (!filters.height.min || tree.height > filters.height.min) &&
          (!filters.height.max || tree.height < filters.height.max) &&
          (!filters.diameter.min || tree.diameter > filters.diameter.min) &&
          (!filters.diameter.max || tree.diameter < filters.diameter.max) &&
          (!filters.date.min || new Date(tree.datePlanted) > filters.date.min) &&
          (!filters.date.max || new Date(tree.datePlanted) < filters.date.max) &&
          (!filters.species || tree.species.name === filters.species) &&
          (!filters.municipality || tree.municipality.name === filters.municipality) &&
          (!filters.status || tree.status === filters.status) &&
          (!filters.land || tree.land === filters.land)) {
        filteredTrees.push(tree);
      }
    });

    this.setState({
      filters: filters,
      mapTrees: filteredTrees
    });
  }

  onResetFilter = () => {
    this.setState((prevState) => ({
      mapTrees: prevState.allTrees,
      filters: {
        age: {min: '', max: ''},
        height: {min: '', max: ''},
        diameter: {min: '', max: ''},
        date: {min: '', max: ''},
        species: '',
        municipality: '',
        land: '',
        status: '',
        ownership: '',
        myTrees: false
      }
    }));
  }

  onDateChange = (day) => this.setState({fcDate: day});
  onFlagChange = (e, {value}) => this.setState({language: value});
  onShowTrees = () => this.setState((prevState) => ({showTrees: !prevState.showTrees}));
  onShowFilters = () => this.setState((prevState) => ({showFilters: !prevState.showFilters}));

  render() {
    const {mapTrees, fcTrees, filters} = this.state;
    const errors = getError(this.state.error);

    const treeIter = [...Array(Math.ceil(this.state.fcTrees.length/8)).keys()];

    const dayPickerProps = {
      locale: this.state.language,
      weekdaysShort: this.state.language === 'hu' ? huDates.weekShort : undefined,
      weekdaysLong: this.state.language === 'hu' ? huDates.weekLong : undefined,
      months: this.state.language === 'hu' ? huDates.months : undefined
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
                    <DayPickerInput placeholder='YYYY-MM-DD' dayPickerProps={{...dayPickerProps, disabledDays: {before: new Date()}}} value={this.state.fcDate} onDayChange={this.onDateChange}/>
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
                    {treeIter.map((i) => (
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
              <Header.Content>
                <Icon name='tree' onClick={this.onShowFilters}/>Tree Map
              </Header.Content>
            </Header>
            <Divider/>
            {this.state.showFilters ? (
              <Form widths='equal'>
                <Form.Group inline>
                  <Form.Input fluid label='Age (years)' placeholder='Min' type='number' min='1' max={filters.age.max} error={errors.height} value={filters.age.min} onChange={this.onMinAgeFilter}/>
                  <Form.Input fluid label='Height (cm)' placeholder='Min' type='number' min='1' max={filters.height.max} error={errors.height} value={filters.height.min} onChange={this.onMinHeightFilter}/>
                  <Form.Input fluid label='Diameter (cm)' placeholder='Min' type='number' min='1' max={filters.diameter.max} error={errors.diameter} value={filters.diameter.min} onChange={this.onMinDiameterFilter}/>
                  <Form.Input label='Date Planted' error={errors.date}>
                    <DayPickerInput placeholder='Min Date' dayPickerProps={{...dayPickerProps, disabledDays: {after: filters.date.max}}} value={filters.date.min} onDayChange={this.onMinDateFilter}/>
                  </Form.Input>
                  <Form.Select fluid options={this.state.speciesSelectable} label='Species' placeholder='Species' error={errors.species} value={filters.species} onChange={this.onSpeciesFilter}/>
                  <Form.Select fluid options={this.state.municipalitySelectable} label='Municipality' placeholder='Municipality' error={errors.municipality} value={filters.municipality} onChange={this.onMunicipalityFilter}/>
                  <Form.Button fluid inverted toggle label='' active={filters.myTrees} color='green' size='small' onClick={this.onMyTreesFilter}>My Trees</Form.Button>
                </Form.Group>
                <Form.Group inline>
                  <Form.Input fluid label='' placeholder='Max' type='number' min={filters.age.min ? filters.age.min : 1} error={errors.height} value={filters.age.max} onChange={this.onMaxAgeFilter}/>
                  <Form.Input fluid label='' placeholder='Max' type='number' min={filters.height.min ? filters.height.min : 1} error={errors.height} value={filters.height.max} onChange={this.onMaxHeightFilter}/>
                  <Form.Input fluid label='' placeholder='Max' type='number' min={filters.diameter.min ? filters.diameter.min : 1} error={errors.diameter} value={filters.diameter.max} onChange={this.onMaxDiameterFilter}/>
                  <Form.Input label='' error={errors.date}>
                    <DayPickerInput placeholder='Max Date' dayPickerProps={{...dayPickerProps, disabledDays: {before: filters.date.min}}} value={filters.date.max} onDayChange={this.onMaxDateFilter}/>
                  </Form.Input>
                  <Form.Select fluid options={statusSelectable} label='Status' placeholder='Status' error={errors.status} value={filters.status} onChange={this.onStatusFilter}/>
                  <Form.Select fluid options={landSelectable} label='Land' placeholder='Land' error={errors.land} value={filters.land} onChange={this.onLandFilter}/>
                  <Form.Button fluid inverted label='' color='blue' size='small' onClick={this.onResetFilter}>Reset Filters</Form.Button>
                </Form.Group>
              </Form>
            ) : null}

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
                <Button inverted color='red' size='small' onClick={() => this.props.onClose(false)}>Close</Button>
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
    mapElement: <div style={{height: '60vh'}}/>
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
      {trees.map((tree) => (
        <Marker
          key={tree.treeId}
          icon={{
            url: getTreeMarker(fcTrees.includes(tree.treeId) ? 'selected' : tree.status),
            anchor: new google.maps.Point(23, 45),
            scaledSize: new google.maps.Size(40, 60)
          }}
          position={getLatLng(tree.location)}
          onClick={() => onTreeClick(tree)}
          onRightClick={() => onTreeRightClick(tree)}
        >
          {hover && hover === tree.treeId ? (
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
  onClose: PropTypes.func.isRequired
};

export default CreateForecastModal;
