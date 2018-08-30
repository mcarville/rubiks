import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import NumberFormat from 'react-number-format';

class App extends Component {

  aggregationLabels = {
	  "userbrowser.keyword": "Browser",
	  "geoip.city_name.keyword": "City",
	  "geoip.country_code2.keyword": "Country code"
  };

  filter = {};
  size = 15;
  page = 0;
  
  constructor(props) {
    super(props);
    this.state = {
      error: null,
      isLoaded: false,
      queryResponse: {},
	  filter: {}
    };
  }

  loadElasticSearchResults() {
	  
	  var filter = this.filter;
	  var query = (this.isNotEmpty(filter)) 
		? {"term" : filter}
		: { "match_all": {} };
	  
	  fetch("elasticsearch/logstash-2018.06.28/_search", {
		  method: 'POST',
		  headers: {
			Accept: 'application/json',
			'Content-Type': 'application/json',
			},
			body: JSON.stringify({
			"from": this.page * this.size, "size": this.size,
			"query": query,
			"aggs" : {
				"userbrowser.keyword" : {"terms" : { "field" : "userbrowser.keyword" }},
				"geoip.city_name.keyword" : {"terms" : { "field" : "geoip.city_name.keyword" }},
				"geoip.country_code2.keyword" : {"terms" : { "field" : "geoip.country_code2.keyword" }},
			}
		  })
	  })
      .then(res => res.json())
      .then(
        (result) => {
          this.setState({
            isLoaded: true,
            queryResponse: result
          });
        },
        // Note: it's important to handle errors here
        // instead of a catch() block so that we don't swallow
        // exceptions from actual bugs in components.
        (error) => {
          this.setState({
            isLoaded: true,
            error
          });
        }
      )
  }
  
  componentDidMount() {
    this.loadElasticSearchResults()
  }

  isNotEmpty (object) {
	  return object != null && Object.keys(object).length > 0;
  }
  
  filterOnAggregation = (aggregationKey, itemKey) => {
	  console.log(aggregationKey + " => " + itemKey);
	  
	  var currentFilter = {};
	  if(this.filter == null || this.filter[aggregationKey] !== itemKey) {
		currentFilter[aggregationKey] = itemKey;
	  }
	  
	  this.filter = currentFilter;
	  this.page = 0;
	  
	  this.loadElasticSearchResults();
  }
  
  goToPage = (page) => {
	  this.page = page;
	  this.loadElasticSearchResults();
  }
  
  render() {
    
	var queryResponse = this.state.queryResponse;

	return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Welcome to React</h1>
        </header>
        <p className="App-intro">
          To get started, edit <code>src/App.js</code> and save to reload.
        </p>
		
		{(this.isNotEmpty (queryResponse) ) ? (
			<div class="search-app " >
			<div class="aggregations" >
					{(this.isNotEmpty (queryResponse.aggregations) ) ? (
						<div>
							{Object.keys(queryResponse.aggregations).map((aggregationKey, bucket) => (
								<div class="aggregation">
									<div key={{aggregationKey}} class="header" >
										{(this.aggregationLabels[aggregationKey] != null ? this.aggregationLabels[aggregationKey] : aggregationKey ) }
									</div>
									<div class="content">
									{queryResponse.aggregations[aggregationKey].buckets.map((item,i) => (
										<div key={item.key} class="item" style={{fontWeight: (this.filter[aggregationKey] === item.key) ? 'bold' : 'normal'}} onClick={(e) => this.filterOnAggregation(aggregationKey, item.key)} >
											{(aggregationKey === 'geoip.country_code2.keyword')  ? (
												<span style={{width: "30px"}}>
													<img src={"/images/flags-mini/" + item.key.toLowerCase() + ".png"} alt={item.key} />&nbsp;
												</span>
											) : (<span></span>)}
											{item.key} (<NumberFormat value={item.doc_count} displayType={'text'} thousandSeparator={true}/>)
										</div>
									))}
									</div>
								</div>
							))}
						</div>
					) : (
						<div>No aggregations</div>
					)}
				</div>
				<div class="results" >
					{(this.isNotEmpty (queryResponse.hits) && this.isNotEmpty (queryResponse.hits.hits) ) ? (
						<div class="content">
							<div style={{overflow: 'auto'}}>
								<div class="headerBox" style={{width: '70%'}}><div class="content" >
									&nbsp;
									<span class="pagerItem actionale" onClick={(e) => this.goToPage(0)}>First</span>
									<span class="pagerItem actionale" onClick={(e) => this.goToPage((this.page > 0) ? this.page - 1 : 0)}>Previous</span>
									<span class="pagerItem">{this.page + 1}</span>
									<span class="pagerItem actionale" onClick={(e) => this.goToPage(this.page + 1)}>Next</span>
								</div></div>
								<div class="headerBox" ><div class="content" >Total: <NumberFormat value={queryResponse.hits.total} displayType={'text'} thousandSeparator={true}/> </div></div>
							</div>
							<div>
								{queryResponse.hits.hits.map((item,i) => (
									<div key={item['_id']} class="result" style={{overflow: 'auto'}}>
										<div class="metadata">{item['_source'].clientip} </div>
										<div class="metadata">
										{(item['_source'].geoip != null && item['_source'].geoip.country_name != null) ? (
											<div>{item['_source'].geoip.city_name} / 
											{(item['_source'].geoip.country_code2 != null)  ? (
												<span>
													&nbsp;
													<img src={"/images/flags-mini/" + item['_source'].geoip.country_code2.toLowerCase() + ".png"} alt={item['_source'].geoip.country_code2}/>
													&nbsp;
												</span>
											) : (<span></span>) }
											{item['_source'].geoip.country_name}
											</div>
										) : (<span>?</span>)}
										</div>
										<div class="metadata">{item['_source'].userbrowser}</div>
									</div>
								))}
							</div>
						</div>
					) : (<div>No results</div>)}
				</div>
			</div>)
			:
			(<div>Nothing to show</div>)
		}
		
      </div>
    );
  }
}

export default App;
